param([string]$SourceRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../..')))
$ErrorActionPreference = 'Stop'
$runtime = Join-Path $SourceRoot 'tmp/user-demo-runtime'
$jar = Join-Path $SourceRoot 'src/backend/target/fin-sync-api-0.0.1-SNAPSHOT.jar'
$pidFile = Join-Path $runtime 'backend.pid'
if (Test-Path $pidFile) {
    $taskProcessId = [int](Get-Content -LiteralPath $pidFile -Raw)
    $server = Get-CimInstance Win32_Process -Filter "ProcessId=$taskProcessId" -ErrorAction SilentlyContinue
    if ($server) {
        if (!$server.CommandLine -or !$server.CommandLine.Contains($jar)) { throw 'PID is not the owned user demo backend; nothing stopped.' }
        Stop-Process -Id $taskProcessId
    }
}
$settings = Get-Content (Join-Path $runtime 'runtime-secrets.json') -Raw | ConvertFrom-Json
$previous = $env:REDISCLI_AUTH
try {
    $env:REDISCLI_AUTH = $settings.redisPassword
    & (Join-Path $runtime 'redis/Redis-7.4.11-Windows-x64-cygwin/redis-cli.exe') -h 127.0.0.1 -p 56379 SHUTDOWN SAVE
} finally { $env:REDISCLI_AUTH = $previous }
Write-Output 'Owned backend/Redis stopped; Redis AOF and PostgreSQL databases preserved. Shared development PostgreSQL stays running.'
