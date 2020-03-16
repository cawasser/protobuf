(ns protobuf.impl.flatland.mapdef
  (:require
    [clojure.java.io :as io]
    [protobuf.impl.flatland.map :as protobuf-map]
    [protobuf.impl.flatland.schema :as protobuf-schema]
    [protobuf.util :as util])
  (:import
    (clojure.lang Reflector)
    (com.google.protobuf CodedInputStream
                         Descriptors$Descriptor
                         Descriptors$FileDescriptor
                         GeneratedMessage)
    (java.io InputStream OutputStream)
    (protobuf Extensions
              PersistentProtocolBufferMap
              PersistentProtocolBufferMap$Def
              PersistentProtocolBufferMap$Def$NamingStrategy))
  (:refer-clojure :exclude [map? read]))

(defn mapdef?
  "Is the given object a `PersistentProtocolBufferMap$Def?`"
  [obj]
  (instance? PersistentProtocolBufferMap$Def obj))

(defn ^PersistentProtocolBufferMap$Def mapdef
  "Create a protocol buffer map definition from a string or protobuf class."
  ([map-def]
   (if (or (mapdef? map-def) (nil? map-def))
     map-def
     (mapdef map-def {})))
  ([map-def opts]
   (when map-def
     (let [{:keys [^PersistentProtocolBufferMap$Def$NamingStrategy naming-strategy
                   size-limit]
            :or   {naming-strategy PersistentProtocolBufferMap$Def/convertUnderscores
                   size-limit      67108864}} opts          ;; 64MiB
           ^Descriptors$Descriptor descriptor
           (if (instance? Descriptors$Descriptor map-def)
             map-def
             (let [d (Reflector/invokeStaticMethod ^Class map-def "getDescriptor" (to-array nil))]
               (cond
                 (= (type d) Descriptors$FileDescriptor) (first (.getMessageTypes d))
                 :else d)))]
       (PersistentProtocolBufferMap$Def/create descriptor (.getFile descriptor) naming-strategy size-limit)))))

(defn create
  "Construct a protobuf of the given map-def."
  ([^PersistentProtocolBufferMap$Def map-def]
   (PersistentProtocolBufferMap/construct map-def {}))
  ([^PersistentProtocolBufferMap$Def map-def m]
   (PersistentProtocolBufferMap/construct map-def m))
  ([^PersistentProtocolBufferMap$Def map-def k v & kvs]
   (PersistentProtocolBufferMap/construct map-def (apply array-map k v kvs))))

(defn mapdef->schema
  "Return the schema for the given mapdef."
  [& args]
  (let [^PersistentProtocolBufferMap$Def map-def (apply mapdef args)]
    (protobuf-schema/field-schema (.getMessageType map-def) map-def)))

(defmulti parse
  "Load a protobuf of the given map-def from a data source.

  Supported data sources are either an array of bytes or an input stream."
  (fn [map-def data & _]
    (type data)))

(defmethod parse (Class/forName "[B")
  ([^PersistentProtocolBufferMap$Def map-def data]
   (when data
     (PersistentProtocolBufferMap/create map-def data)))
  ([^PersistentProtocolBufferMap$Def map-def data ^Integer offset ^Integer length]
   (when data
     (let [^CodedInputStream in (CodedInputStream/newInstance data offset length)]
       (PersistentProtocolBufferMap/parseFrom map-def in)))))

(defmethod parse InputStream
  [^PersistentProtocolBufferMap$Def map-def stream]
  (when stream
    (let [^CodedInputStream in (CodedInputStream/newInstance stream)]
      (PersistentProtocolBufferMap/parseFrom map-def in))))

(defmethod parse CodedInputStream
  [^PersistentProtocolBufferMap$Def map-def ^CodedInputStream stream]
  (PersistentProtocolBufferMap/parseFrom map-def stream))

(defn ^"[B" ->bytes
  "Return the byte representation of the given protobuf."
  [^PersistentProtocolBufferMap$Def map-def m]
  (protobuf-map/->bytes (PersistentProtocolBufferMap/construct map-def m)))

(defn read
  "Lazily read a sequence of length-delimited protobufs of the specified map-def
  from the given input stream."
  [^PersistentProtocolBufferMap$Def map-def in]
  (lazy-seq
    (io!
      (let [^InputStream in (io/input-stream in)]
        (if-let [p (PersistentProtocolBufferMap/parseDelimitedFrom map-def in)]
          (cons p (read map-def in))
          (.close in))))))

(defn write
  "Write the given protobufs to the given output stream, prefixing each with
  its length to delimit them."
  [out & ps]
  (io!
    (let [^OutputStream out (io/output-stream out)]
      (doseq [^PersistentProtocolBufferMap p ps]
        (.writeDelimitedTo p out))
      (.flush out))))

(defn syntax
  [^PersistentProtocolBufferMap$Def map-def]
  (.getSyntax (.file map-def)))

(extend-protocol util/Combiner
  PersistentProtocolBufferMap
  (combine-onto [^PersistentProtocolBufferMap this other]
    (.append this other)))

(def ^{:doc "A convenience alias for `util/combine`"}
  combine #'util/combine)



(comment
  ; testing mapdef on syntax3 nested types
  (import 'protobuf.examples.message3.Person3)

  (def map-def Person3)
  (def opts {})
  (def d (Reflector/invokeStaticMethod ^Class map-def "getDescriptor" (to-array nil)))

  (.getSyntax d)
  (.toProto d)
  (.getMessageTypes d)
  (first (.getMessageTypes d))
  (instance? Descriptors$Descriptor (first (.getMessageTypes d)))

  (def descriptor (if (instance? Descriptors$Descriptor map-def)
                    map-def
                    (let [d (Reflector/invokeStaticMethod ^Class map-def "getDescriptor" (to-array nil))]
                      (cond
                        (= (type d) Descriptors$FileDescriptor) (first (.getMessageTypes d))))))

  (let [{:keys [^PersistentProtocolBufferMap$Def$NamingStrategy naming-strategy
                size-limit]
         :or   {naming-strategy PersistentProtocolBufferMap$Def/convertUnderscores
                size-limit      67108864}} opts]
    [naming-strategy size-limit])

  (def naming-strategy PersistentProtocolBufferMap$Def/convertUnderscores)
  (def size-limit 67108864)

  (PersistentProtocolBufferMap$Def/create
    descriptor
    (.getFile descriptor)
    naming-strategy
    size-limit)
  (mapdef map-def)


  ; testing some of the "simpler" examples
  (import 'protobuf.testing.Core$Foo)

  (instance? Descriptors$Descriptor Core$Foo)
  (Reflector/invokeStaticMethod ^Class Core$Foo "getDescriptor" (to-array nil))
  (mapdef Core$Foo)

  ())