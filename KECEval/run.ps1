# PowerShell application launcher
param (
    [switch]$Demo
)

if ($Demo) {
    java -cp ".;mysql-connector-j-8.4.0.jar" MainApp --demo
} else {
    java -cp ".;mysql-connector-j-8.4.0.jar" MainApp
}

