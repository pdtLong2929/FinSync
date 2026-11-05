param([string]$SourceRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../..')))
$ErrorActionPreference = 'Stop'
$runtime = Join-Path $SourceRoot 'tmp/user-demo-runtime'
$pgRuntime = Join-Path $SourceRoot 'tmp/auth-runtime-20261104'
$jar = Join-Path $SourceRoot 'src/backend/target/fin-sync-api-0.0.1-SNAPSHOT.jar'
$settingsPath = Join-Path $runtime 'runtime-secrets.json'
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'C:\Program Files\Android\Android Studio\jbr\bin\java.exe' }
if (!(Test-Path $settingsPath) -or !(Test-Path $jar)) {
    throw 'Prepared local demo runtime/backend JAR missing. See USER_ACCOUNT_INTEGRATION.md; no credentials are bundled in Git.'
}
$settings = Get-Content -LiteralPath $settingsPath -Raw | ConvertFrom-Json
$pgCtl = Join-Path $pgRuntime 'pgsql/bin/pg_ctl.exe'
& $pgCtl -D (Join-Path $pgRuntime 'pgdata') status *> $null
if ($LASTEXITCODE -ne 0) {
    & $pgCtl -D (Join-Path $pgRuntime 'pgdata') -l (Join-Path $pgRuntime 'postgres.log') -o '-h 127.0.0.1 -p 55432' -w start
    if ($LASTEXITCODE -ne 0) { throw 'Dedicated PostgreSQL cluster could not start.' }
}
function Get-OwnedProcess($pidPath, $executableMarker) {
    if (!(Test-Path $pidPath)) { return $null }
    $taskProcessId = [int](Get-Content -LiteralPath $pidPath -Raw)
    $taskProcess = Get-CimInstance Win32_Process -Filter "ProcessId=$taskProcessId" -ErrorAction SilentlyContinue
    if ($taskProcess -and (!$taskProcess.CommandLine -or !$taskProcess.CommandLine.Contains($executableMarker))) {
        throw 'Recorded PID belongs to a different process; nothing changed.'
    }
    return $taskProcess
}
# Replace only the previously owned legacy auth server; preserve its database.
$legacyJar = Join-Path $SourceRoot 'tmp/backend-auth-20261104/src/backend/target/fin-sync-api-0.0.1-SNAPSHOT.jar'
$legacyPid = Join-Path $pgRuntime 'backend.pid'
if (Test-Path $legacyPid) {
    $legacy = Get-CimInstance Win32_Process -Filter ('ProcessId=' + [int](Get-Content $legacyPid -Raw)) -ErrorAction SilentlyContinue
    if ($legacy -and $legacy.CommandLine -and $legacy.CommandLine.Contains($legacyJar)) { Stop-Process -Id $legacy.ProcessId }
}
$redisExe = Join-Path $runtime 'redis/Redis-7.4.11-Windows-x64-cygwin/redis-server.exe'
if (!(Get-OwnedProcess (Join-Path $runtime 'redis.pid') $redisExe)) {
    $redis = Start-Process -FilePath $redisExe -ArgumentList @('redis.conf') -WorkingDirectory $runtime -WindowStyle Hidden -PassThru `
        -RedirectStandardOutput (Join-Path $runtime 'redis.log') -RedirectStandardError (Join-Path $runtime 'redis-error.log')
    $redis.Id | Set-Content (Join-Path $runtime 'redis.pid')
}
$pidFile = Join-Path $runtime 'backend.pid'
$previousRedisAuth = $env:REDISCLI_AUTH
try {
    $env:REDISCLI_AUTH = $settings.redisPassword
    $redisReady = $false
    $redisDeadline = (Get-Date).AddSeconds(10)
    do {
        $pong = & (Join-Path $runtime 'redis/Redis-7.4.11-Windows-x64-cygwin/redis-cli.exe') -h 127.0.0.1 -p 56379 PING 2>$null
        $redisReady = $pong -eq 'PONG'
        if (!$redisReady) { Start-Sleep -Milliseconds 500 }
    } while (!$redisReady -and (Get-Date) -lt $redisDeadline)
    if (!$redisReady) { throw 'Dedicated Redis did not pass its authenticated readiness check.' }
} finally { $env:REDISCLI_AUTH = $previousRedisAuth }
if (!(Get-OwnedProcess $pidFile $jar)) {
    if (Get-NetTCPConnection -State Listen -LocalPort 18080 -ErrorAction SilentlyContinue) { throw 'Port18080 belongs to another process; nothing stopped.' }
    $values = @{
        DB_URL='jdbc:postgresql://127.0.0.1:55432/finsync_userdemo'; DB_USERNAME='finsync_authdev'; DB_PASSWORD=$settings.dbPassword
        JWT_SECRET=$settings.jwtSecret; SERVER_PORT='18080'; SERVER_ADDRESS='127.0.0.1'
        REDIS_HOST='127.0.0.1'; REDIS_PORT='56379'; REDIS_PASSWORD=$settings.redisPassword; SPRING_DATA_REDIS_DATABASE='0'
    }
    $previous = @{}
    try {
        foreach ($key in $values.Keys) {
            $previous[$key] = [Environment]::GetEnvironmentVariable($key, 'Process')
            [Environment]::SetEnvironmentVariable($key, $values[$key], 'Process')
        }
        $server = Start-Process -FilePath $java -ArgumentList @('-Xmx512m','-jar',('"{0}"' -f $jar)) -WindowStyle Hidden -PassThru `
            -RedirectStandardOutput (Join-Path $runtime 'backend.log') -RedirectStandardError (Join-Path $runtime 'backend-error.log')
        $server.Id | Set-Content -LiteralPath $pidFile
    } finally {
        foreach ($key in $previous.Keys) { [Environment]::SetEnvironmentVariable($key, $previous[$key], 'Process') }
    }
}
$ready = $false
$deadline = (Get-Date).AddSeconds(90)
do {
    try { Invoke-WebRequest 'http://127.0.0.1:18080/api/v1/auth/login' -Method Post -ContentType 'application/json' -Body '{}' -TimeoutSec 2 | Out-Null }
    catch { if ($_.Exception.Response -and [int]$_.Exception.Response.StatusCode -eq 400) { $ready = $true } }
    if (!$ready) { Start-Sleep -Seconds 2 }
} while (!$ready -and (Get-Date) -lt $deadline)
if (!$ready) { throw 'Backend readiness failed; inspect private runtime logs.' }
Write-Output 'User demo ready: API localhost:18080, PostgreSQL localhost:55432/finsync_userdemo, Redis localhost:56379.'
Write-Output 'Android emulator uses http://10.0.2.2:18080. Financial screens remain local.'
