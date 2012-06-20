(ns bkell.run.run-ring
  (:require [noir.server :as server]
            [bkell.bjell :as bjell]
            [bkell.bkell :as bkell]
  ))


(server/load-views "src/bkell/http/") 

; the default mode is 'dev', 
; for heroku, you can set the environment variable with the command: 
; `heroku config:add MODE=prod`
(defn -main [& m]
  (let[ config (load-file "etc/config/config.clj")
        mode (keyword (get (System/getenv) "MODE" "dev"))
        host-port (or (first m)                   ;; see if PORT is passed in as a parameter
                      (-> config mode :host-port) ;; otherwise, get the PORT from the config mode
                      (get (System/getenv) "PORT" "8080"))  ;; last ditch is to set the PORT manually to 8080
      ]
    
    
    (println (str "Initializing shell with mode: " mode))
    
    ;; ====
    ;; Initialize the shell incl. DB connection 
    (bjell/init-shell mode { :host-port host-port })
    
    
    ;; ==== 
    ;; Setting the mode in the shell 
    (dosync 
      (alter bkell/shell conj { :mode mode })
      (let [new-prod (merge (:prod config) { :host-port host-port })]
        (alter bkell/shell conj (merge config { :prod new-prod }))
      )
    )
    
    (println (str "Bkell: " @bkell/shell))
    
    ;; ====
    ;; Startup the Noir server (wraps Jetty)
    (server/start (Integer. host-port) {  :mode mode :ns 'bkell  })
    
  ))

