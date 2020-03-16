set OUT_DIR=target\examples

md %OUT_DIR%

protoc ^
  -I="C:\Program Files\Protoc\include" ^
  -I=resources ^
  --java_out=%OUT_DIR% ^
  resources\protobuf\examples\*.proto
