param()

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$workspace = Split-Path -Parent $scriptDir
$logsDir = Join-Path $workspace 'integrated-test-plan/logs'
New-Item -ItemType Directory -Path $logsDir -Force | Out-Null

$categorySamples = @(
    [pscustomobject]@{
        Category = 'sides'
        SamplePath = 'changedfiles/ATG/sides/side2-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            $updated = [regex]::Replace($content, '"units":\s*"[^"]+"', '"units": "1"', 1)
            if ($updated -eq $content) {
                $updated = $content -replace '("units":\s*)"[^"]+"', '$1"1"'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'transactions'
        SamplePath = 'changedfiles/ATG/transactions/Buy-default-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            $updated = [regex]::Replace($content, '"description":\s*"[^"]+"', '"description": "A similar buy type - updated"', 1)
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'coa'
        SamplePath = 'changedfiles/ATG/coa/USG-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"[^"]+"', '"description": "Standard COA updated"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'gla'
        SamplePath = 'changedfiles/ATG/gla/USG-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"([^"]+)"', '"description": "Cash New - updated"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'postingrules'
        SamplePath = 'changedfiles/ATG/postingrules/USG-USG-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"[^"]+"', '"description": "PostingModuleDescription Updated"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'glprofile'
        SamplePath = 'changedfiles/ATG/glprofile/USG-USG_GP-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"[^"]+"', '"description": "GLP description updated via automation"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'derivedportfolios'
        SamplePath = 'changedfiles/ATG/derivedportfolios/derivedportfolios-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"[^"]+"', '"description": "Derived Portfolio Batch Update"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'portfoliogroups'
        SamplePath = 'changedfiles/ATG/portfoliogroups/portfoliogroups-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"[^"]+"', '"description": "Portfolio group metadata refreshed"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'aborconfigs'
        SamplePath = 'changedfiles/ATG/aborconfigs/aborconfig-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"([^"]+)"', '"description": "Standard AborConfigurationRequest - updated"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    },
    [pscustomobject]@{
        Category = 'abor'
        SamplePath = 'changedfiles/ATG/abor/abor-ATG.json'
        UpdateScript = {
            param($path)
            $content = Get-Content -Raw $path
            if ($content -match '"description":\s*"[^"]+"') {
                $updated = [regex]::Replace($content, '"description":\s*"([^"]+)"', '"description": "A standard Abor updated"', 1)
            } else {
                $updated = $content -replace '(}\s*)$', ', "_integrationUpdated": true$1'
            }
            Set-Content -Path $path -Value $updated
        }
    }
)

$changedRoot = Join-Path $workspace 'changedfiles'
if (-not (Test-Path $changedRoot)) {
    Write-Error "Changed files root not found at $changedRoot"
    exit 1
}

mvn -DskipTests clean package | Tee-Object (Join-Path $logsDir 'mvn.log')

$jar = Get-ChildItem -Path (Join-Path $workspace 'target') -Filter '*cac-ex*.jar' -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $jar) {
    Write-Error 'Could not find built jar in target/.'
    exit 1
}

function Run-Command {
    param (
        [string]$Name,
        [string[]]$Flags,
        [string]$LogPath
    )

    Write-Host "`n== Running $Name =="
    Write-Host "Executing: java -jar $($jar.FullName) $($Flags -join ' ')"
    & java -jar $jar.FullName @Flags 2>&1 | Tee-Object $LogPath
    if ($LASTEXITCODE -ne 0) {
        Throw "$Name failed (see $LogPath)"
    }
}

function Get-SamplePaths {
    $categorySamples | ForEach-Object {
        $absolute = Join-Path $workspace $_.SamplePath
        if (-not (Test-Path $absolute)) {
            Throw "Sample file not found: $absolute"
        }
        $absolute
    }
}

$updateBackups = [System.Collections.Generic.List[pscustomobject]]::new()

function Modify-SampleFiles {
    $updateBackups.Clear()
    $paths = [System.Collections.Generic.List[string]]::new()
    foreach ($sample in $categorySamples) {
        $path = Join-Path $workspace $sample.SamplePath
        $backup = "$path.updatebak"
        Copy-Item -Path $path -Destination $backup -Force
        $updateBackups.Add([pscustomobject]@{ Original = $path; Backup = $backup })
        & $sample.UpdateScript $path
        $paths.Add($path)
    }
    $paths
}

function Restore-ModifiedFiles {
    foreach ($entry in $updateBackups) {
        if (Test-Path $entry.Backup) {
            Move-Item -Path $entry.Backup -Destination $entry.Original -Force
        }
    }
    $updateBackups.Clear()
}

function Hide-FilesTemporarily {
    param (
        [string[]]$Paths,
        [scriptblock]$Action
    )

    $backups = @()
    foreach ($path in $Paths) {
        if (-not (Test-Path $path)) {
            continue
        }
        $backup = "$path.deletebak"
        Move-Item -Path $path -Destination $backup -Force
        $backups += [pscustomobject]@{ Original = $path; Backup = $backup }
    }

    try {
        & $Action
    } finally {
        foreach ($entry in $backups) {
            if (Test-Path $entry.Backup) {
                Move-Item -Path $entry.Backup -Destination $entry.Original -Force
            }
        }
    }
}

$stages = @(
    [pscustomobject]@{ Name = 'create'; GetPaths = { Get-SamplePaths }; Cleanup = { } },
    [pscustomobject]@{ Name = 'update'; GetPaths = { Modify-SampleFiles }; Cleanup = { Restore-ModifiedFiles } },
    [pscustomobject]@{ Name = 'delete'; GetPaths = { Get-SamplePaths }; Cleanup = { } }
)

foreach ($stage in $stages) {
    Write-Host "`n=== Stage: $($stage.Name.ToUpper()) ==="
    $paths = & $stage.GetPaths
    if ($null -eq $paths -or $paths.Count -eq 0) {
        Write-Warning "Skipping $($stage.Name) stage because no paths were generated."
        continue
    }

    $env:CHANGED_FILES = ($paths | Sort-Object) -join ' '
    Write-Host "CHANGED_FILES set to" $env:CHANGED_FILES

    $planLog = Join-Path $logsDir "plan-$($stage.Name).log"
    $applyLog = Join-Path $logsDir "apply-$($stage.Name).log"

    $action = {
        Run-Command -Name "plan ($($stage.Name))" -Flags @('--plan') -LogPath $planLog
        Run-Command -Name "apply ($($stage.Name))" -Flags @('--apply') -LogPath $applyLog
    }

    if ($stage.Name -eq 'delete') {
        Hide-FilesTemporarily -Paths $paths -Action $action
    } else {
        & $action
    }

    if ($stage.Cleanup) {
        & $stage.Cleanup
    }
}
