rootProject.name = "armeria-api-gateway"

include("server")

buildCache {
    local {
        isEnabled = true
    }
}
