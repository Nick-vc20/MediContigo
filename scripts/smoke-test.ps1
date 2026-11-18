$ErrorActionPreference = "Stop"

$backendUrl = if ($env:BACKEND_URL) {
    $env:BACKEND_URL.TrimEnd('/')
} else {
    "https://medicontigo-backend.onrender.com"
}

$endpoints = @(
    @{
        Name = "Health Check"
        Url = "$backendUrl/api/health"
        Check = "health"
    },
    @{
        Name = "Centro medico publico"
        Url = "$backendUrl/api/publico/centros/1"
        Check = "centro"
    }
)

foreach ($endpoint in $endpoints) {
    Write-Host "Verificando: $($endpoint.Name)"

    $respuesta = Invoke-WebRequest -Uri $endpoint.Url -TimeoutSec 120
    $datos = $respuesta.Content | ConvertFrom-Json

    if ($respuesta.StatusCode -ne 200) {
        throw "$($endpoint.Name): HTTP $($respuesta.StatusCode)"
    }

    if ($endpoint.Check -eq "health") {
        if ($datos.status -ne "UP" -or $datos.database -ne "UP") {
            throw "Health Check incorrecto"
        }
    }

    Write-Host "OK: $($endpoint.Name)"
}

Write-Host "SMOKE TESTS EXITOSOS"
