(ns bjell

  (:import java.io.FileReader)
  (:use somnium.congomongo)
  (:use clojure.contrib.debug)
  
  (:require [bkell]
            [clojure.data.json]
            [domain]
            [util])

)


(defn init-shell [] 
  
  (somnium.congomongo/mongo! :db "bkell") ;; connect to mongodb
  (def shell (ref { :active true })) 	;; the shell and memory 
)


(defn add [artifact & etal]
  "artifact - input can be JSON String or Reader"
  
  (let  [ artifact-p (domain/keywordize-tags (clojure.data.json/read-json artifact))]

      (domain/bsonid-to-id
        (eval `(bkell/add ~artifact-p ~@etal)) )

  )
)


(defn get [akey & etal]
  "akey - input is a String"
    (let [result (eval `(bkell/get ~akey ~@etal))]

      (if (or (vector? result)
              (list? result)
              (empty? result))
        result
        (domain/bsonid-to-id result)
      )
    )
)

(defn update [artifact & etal]
  "artifact - input can be JSON String or Reader"
  
  (let  [ artifact-p (domain/keywordize-tags (clojure.data.json/read-json artifact))]
    (domain/bsonid-to-id
      (eval `(bkell/update ~artifact-p ~@etal)) )
  )
)

(defn remove [entity & etal]
  "entity - input is a String"
  
    (let [result (eval `(bkell/remove ~entity ~@etal))]
      result
      #_(if (-> result nil? not)
        (clojure.data.json/json-str result)
        result
      )
    )
)

(defn login [user]
  (let  [ user-p (domain/keywordize-tags (clojure.data.json/read-json user))]
    (-> user-p bkell/login domain/bsonid-to-id ))

)

