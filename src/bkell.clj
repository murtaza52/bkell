
(ns bkell
   (:import com.interrupt.bookkeeping.cc.parser.Parser) 
   (:import com.interrupt.bookkeeping.cc.lexer.Lexer) 
   (:import java.io.PushbackReader) 
   (:import java.io.InputStreamReader) 
   
   (:use clojure.core)
   
)

(defn get-parser [] 
	(Parser. (Lexer. (PushbackReader. (InputStreamReader. java.lang.System/in) 1024)))
)

(defn init-shell [] 
	(def shell (ref { :active true })) 	;; the shell and memory 
)

(defn bkell [handler] 
	
	(init-shell)
	
	(loop [ dfadapter handler ] 	;; binds 'handler' to 'dfadapter' 
		
		(def tree (.parse (get-parser))) 
		(. tree apply dfadapter )
		
		(if (true? (:active (deref bkell/shell)))				;; loop unless exit 
		   (recur dfadapter)
		)
	)
	
)

;;(use 'depth_adapter)
;;(bkell (get-depth-adapter))
