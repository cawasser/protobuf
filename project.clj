(defproject clojusc/protobuf "3.6.0-v1.2-SNAPSHOT"
  :description "A Clojure interface to Google's protocol buffers"
  :url "https://github.com/clojusc/protobuf"
  :license {
    :name "Eclipse Public License"
    :url "http://www.eclipse.org/legal/epl-v10.html"}
  :exclusions [
    [org.clojure/clojure]]
  :dependencies [
    [com.google.protobuf/protobuf-java "3.6.0"]
    [gloss "0.2.6"]
    [org.clojure/clojure "1.9.0"]
    [org.flatland/io "0.3.0"]]
  :source-paths [
    "src/clj"
    "target/extensions"]
  :java-source-paths ["src/java"]
  :jvm-opts ["-Dprotobuf.impl=flatland"]
  :aot [protobuf.impl.flatland.core]
  :profiles {
    :ubercompile {
      :aot :all}
    :custom-repl {
      :source-paths ["dev-resources/src"]
      :repl-options {
        :init-ns protobuf.dev
        :prompt ~#(str "\u001B[35m[\u001B[34m"
                       %
                       "\u001B[35m]\u001B[33m λ\u001B[m=> ")}}
    :test {
      :plugins [
        [jonase/eastwood "0.2.8"]
        [lein-ancient "0.6.15"]
        [lein-ltest "0.3.0"]
        [lein-shell "0.5.0"]]
      :java-source-paths [
        "target/examples"
        "target/testing"]}
    :docs {
      :dependencies [
        [clojang/codox-theme "0.2.0-SNAPSHOT"]]
      :plugins [
        [lein-codox "0.10.4"]
        [lein-marginalia "0.9.1"]]
      :codox {
        :project {
          :name "protobuf"
          :description "A Clojure interface to Google's protocol buffers"}
        :namespaces [#"^protobuf\.(?!dev)"]
        :metadata {
          :doc/format :markdown
          :doc "Documentation forthcoming"}
        :themes [:clojang]
        :doc-paths ["resources/docs"]
        :output-path "docs/current"}}
    ; :1.5 {:dependencies [[org.clojure/clojure "1.5.0"]]}
    ; :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
    ; :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
    :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
    :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}}
  :aliases {
    ;; Dev tasks
    "ubercompile" [
      "with-profile"
      "+ubercompile"
      "compile"]
    "repl" [
      "with-profile"
      "+test,+custom-repl"
      "do"
      ["clean"]
      ["protoc-all"]
      ["repl"]]
    ;; Doc-generation
    "clojuredocs" [
      "with-profile"
      "+docs"
      "codox"]
    "javadocs" [
      "with-profile"
      "+test"
      "shell"
      "bin/javadoc"]
    "docs" [
      "do"
      ["clojuredocs"]
      ["marg" "--dir" "docs/current"
        "--file" "marginalia.html"
        "--name" "Clojure Protocol Buffer Library"]
      ["javadocs"]]
    ;; Protobuf compilation tasks
    "protoc-examples" ["shell" "make" "examples"]
    "protoc-extensions" ["shell" "make" "extensions"]
    "protoc-testing" ["shell" "make" "testing"]
    "protoc-all" ["shell" "make" "protobufs"]
    ;; Deps, linting, and tests
    "check-deps" [
      "with-profile"
      "+test"
      "ancient"
      "check"
      ":all"]
    "lint" [
      "with-profile"
      "+test"
      "eastwood"
      "{:namespaces [:source-paths] :source-paths [\"src/clj\"]}"]
    "ltest" [
      "with-profile"
      "+test"
      "do"
      ["clean"] ["protoc-all"] ["ltest"]]
    "test-old-clojure" [
      "with-profile"
      "+test,+1.7"
      "test"]
    "test-new-clojure" [
      "with-profile"
      "+test,+1.8:+test,+1.9"
      "ltest"]
    "test-all" [
      "do"
      ["clean"]
      ["protoc-all"]
      ; ["test-old-clojure"]
      ["clean"]
      ["protoc-all"]
      ["test-new-clojure"]]
    "clean-test" [
      "do"
      ["clean"]
      ["protoc-all"]
      ["ltest"]]
    "clean-test-all" [
      "do"
      ["clean"]
      ["protoc-all"]
      ["test-all"]]
    "build-test" [
      "do"
      ["clean"]
      ["ubercompile"]
      ["clean"]
      ["protoc-all"]
      ["test-new-clojure"]
      ["lint"]]})
