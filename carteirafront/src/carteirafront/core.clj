(ns carteirafront.core
  (:gen-class))

(defn menu []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "=+= Gerenciador de Carteira de Acoes =+=")
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "\nDigite a opcao desejada:")
  (println "1 - Consultar acao")
  (println "2 - Comprar acao")
  (println "3 - Vender acao")
  (println "4 - Exibir extrato")
  (println "5 - Exibir saldo\n") 

  (let
   [opcao (read)]
    (cond
      (= opcao 0) (println "0")
      ;(= opcao 1) (consultar)
      ;(= opcao 2) (comprar)
      ;(= opcao 3) (vender)
      ;(= opcao 4) (exibir-extrato)
      ;(= opcao 5) (exibir-saldo)
      :else (println "..."))
    (recur)))

(defn -main
  [& args]
  (menu)
  )
