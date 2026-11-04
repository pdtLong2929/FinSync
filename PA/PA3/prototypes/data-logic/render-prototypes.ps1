param([string]$Only = '')
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../..'))
$output = Join-Path $root 'screenshots/prototypes/data-logic'
New-Item -ItemType Directory -Force -Path $output | Out-Null
$data = Get-Content -Raw -Encoding UTF8 (Join-Path $PSScriptRoot 'screens.json') | ConvertFrom-Json
$script:overflow = [Collections.Generic.List[string]]::new()
$fonts = @{}
function Font([int]$size, [bool]$bold = $false) {
    $key = "$size-$bold"
    if (-not $fonts.ContainsKey($key)) {
        $style = if ($bold) { [Drawing.FontStyle]::Bold } else { [Drawing.FontStyle]::Regular }
        $fonts[$key] = [Drawing.Font]::new('Segoe UI', $size, $style, [Drawing.GraphicsUnit]::Pixel)
    }
    return $fonts[$key]
}
function Brush([string]$color) { return [Drawing.SolidBrush]::new([Drawing.ColorTranslator]::FromHtml($color)) }
function Box($g, [string]$color, [int]$x, [int]$y, [int]$w, [int]$h) {
    $b = Brush $color
    $g.FillRectangle($b, $x, $y, $w, $h)
    $b.Dispose()
}
function Text($g, [string]$text, [int]$size, [bool]$bold, [string]$color, [int]$x, [int]$y, [int]$w, [int]$h, [string]$label) {
    $f = Font $size $bold
    $measured = $g.MeasureString($text, $f, $w)
    while ($measured.Height -gt $h -and $size -gt 13) {
        $size--
        $f = Font $size $bold
        $measured = $g.MeasureString($text, $f, $w)
    }
    if ($measured.Height -gt $h + 1) { $script:overflow.Add("$label : $($measured.Height) > $h") }
    $b = Brush $color
    $rect = [Drawing.RectangleF]::new($x, $y, $w, $h)
    $g.DrawString($text, $f, $b, $rect)
    $b.Dispose()
}
$count = 0
foreach ($case in $data.cases) {
    if ($Only -and $case.id -ne $Only) { continue }
    foreach ($board in $case.boards) {
        $screens = @($board.screen_ids | ForEach-Object { $sid = $_; $case.screens | Where-Object { $_.id -eq $sid } })
        $cols = 3
        $rows = [int][Math]::Ceiling($screens.Count / 3.0)
        $width = 1440
        $height = 170 + $rows * 700 + 30
        $bitmap = [Drawing.Bitmap]::new($width, $height)
        $g = [Drawing.Graphics]::FromImage($bitmap)
        $g.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::AntiAlias
        $g.TextRenderingHint = [Drawing.Text.TextRenderingHint]::AntiAliasGridFit
        $g.Clear([Drawing.ColorTranslator]::FromHtml('#f3f1fa'))
        Box $g '#21183b' 0 0 $width 142
        Text $g 'FINSYNC  /  DATA & LOGIC' 20 $true '#cdbaff' 30 19 1000 28 'board-label'
        Text $g ($case.id + '  ' + $case.title) 29 $true '#ffffff' 30 52 1380 46 'board-title'
        Text $g 'Specification wireframes v1 | Illustrative values | Design export, not execution evidence' 17 $false '#ded5f0' 30 106 1380 28 'board-note'
        for ($i = 0; $i -lt $screens.Count; $i++) {
            $s = $screens[$i]
            $x = 30 + ($i % $cols) * 466
            $y = 166 + [int][Math]::Floor($i / $cols) * 700
            Box $g '#ded7eb' ($x + 3) ($y + 4) 448 678
            Box $g '#ffffff' $x $y 448 678
            Box $g '#7850cd' $x $y 448 5
            Text $g $s.id 17 $true '#6b42bd' ($x+20) ($y+20) 408 25 ($s.id+'-id')
            Text $g $s.title 25 $true '#172036' ($x+20) ($y+56) 408 65 ($s.id+'-title')
            Text $g $s.tag 15 $false '#687080' ($x+20) ($y+123) 408 55 ($s.id+'-tag')
            $cursor = $y + 184
            foreach ($field in $s.fields) {
                $fieldHeight = [int][Math]::Max(42, [Math]::Ceiling($g.MeasureString($field, (Font 17), 388).Height) + 14)
                Box $g '#f5f2fb' ($x+20) $cursor 408 $fieldHeight
                Text $g $field 17 $false '#34405a' ($x+30) ($cursor+6) 388 ($fieldHeight-10) ($s.id+'-field')
                $cursor += $fieldHeight + 6
            }
            $cursor += 10
            Text $g $s.message 18 $false '#28364d' ($x+20) $cursor 408 ($y+578-$cursor) ($s.id+'-message')
            Box $g '#7850cd' ($x+20) ($y+594) 408 39
            Text $g $s.action 17 $true '#ffffff' ($x+30) ($y+601) 388 25 ($s.id+'-action')
            Text $g ($s.secondary + '   |   Specification state only') 14 $false '#6f7280' ($x+20) ($y+643) 408 22 ($s.id+'-footer')
        }
        $bitmap.Save((Join-Path $output $board.filename), [Drawing.Imaging.ImageFormat]::Png)
        $g.Dispose()
        $bitmap.Dispose()
        $count++
    }
}
foreach ($f in $fonts.Values) { $f.Dispose() }
if ($script:overflow.Count) { $script:overflow | Set-Content -Encoding UTF8 (Join-Path $PSScriptRoot 'render-overflow.txt'); throw "Text overflow in $($script:overflow.Count) fields" }
Write-Output "Exported $count PNG boards; no measured text overflow."
