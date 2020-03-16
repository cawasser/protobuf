set OUT_DIR=target\testing

md %OUT_DIR%

protoc ^
  -I="C:\Program Files\Protoc\include" ^
  -I=resources ^
  --java_out=%OUT_DIR% ^
  resources\protobuf\testing\*.proto
