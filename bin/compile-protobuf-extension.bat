set OUT_DIR=src\java

md %OUT_DIR%

protoc ^
  -I="C:\Program Files\Protoc\include" ^
  -I=resources ^
  --java_out=%OUT_DIR% ^
  resources\protobuf\extensions.proto
