(ns carteirafront.core
  (:require
   [cheshire.core :as json]
   [clj-http.client :as http-client])
  (:gen-class))



;;Variaveis de ambiente
(def APIBASEURL "http://localhost:3000")

;;Common
(defn respostaValida? [response]
  (and
   (some? response)

   (map? response)
   (some? (:body response))

   (not (string? (:body response)))

   (not (contains? (:body response) :mensagem))
   (not= "erro" (:status (:body response)))))

(defn imprimirExtrato [response]
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println
   (format "%-12s %-10s %-10s %-12s %-12s"
           "DATA"
           "TIPO"
           "AÇÃO"
           "QTD"
           "PREÇO"))

  (let [extrato (:body response)]
    (doseq [{:keys [data tipo acao quantidade preco]} extrato]
      (println
       (format "%-12s %-10s %-10s %-12d %-12.2f"
               data tipo acao quantidade preco)))))

(defn imprimirSaldo [response]
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println
   (format "%-10s %-10s"
           "Acao"
           "Quantidade"))

  (doseq [[acao qtd] response]
    (println
     (format "%-12s %-12d"
             (name acao)
             qtd))))

(defn imprimirConsulta [vetor]
  (println "\n+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "Acao:"     (nth vetor 0))
  (println "Abertura:" (nth vetor 1))
  (println "Alta:"     (nth vetor 2))
  (println "Baixa:"    (nth vetor 3))
  (println "Preco:"    (nth vetor 4))
  (println "Fechamento:" (nth vetor 5))
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+\n"))

(defn imprimirCompra [vetor]
  (println "\n+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "Data:"     (nth vetor 0))
  (println "Acao:"     (nth vetor 1))
  (println "Quantidade comprada:" (nth vetor 2))
  (println "Preco unitario:"     (nth vetor 3))
  (println "Preco total:"    (nth vetor 4))
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+\n"))

(defn imprimirVenda [vetor]
  (println "\n+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "Data:"     (nth vetor 0))
  (println "Acao:"     (nth vetor 1))
  (println "Quantidade vendida:" (nth vetor 2))
  (println "Preco unitario:"     (nth vetor 3))
  (println "Preco total:"    (nth vetor 4))
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+\n"))

;;"controller"
(defn requestConsultaAcao [symbol]
  (let [url (str APIBASEURL "/acao/" symbol)
        response (http-client/get url {:as :json})]
    response))

(defn parseConsultaAcao [response]
  (let [acao (get-in response [:body :acao])
        abertura (get-in response [:body :abertura])
        alta (get-in response [:body :alta])
        baixa (get-in response [:body :baixa])
        preco (get-in response [:body :preco])
        fechamento (get-in response [:body :close])]
    [acao abertura alta baixa preco fechamento]))


(defn requestCompraAcao [symbol qtd data]
  (http-client/post (str APIBASEURL "/compra")
                    {:headers {"Content-Type" "application/json"}
                     :as :json
                     :body (json/generate-string {:symbol symbol
                                                  :quantidade qtd
                                                  :data data})}))

(defn requestVendeAcao [symbol qtd data]
  (http-client/post (str APIBASEURL "/vende")
                    {:headers {"Content-Type" "application/json"}
                     :as :json
                     :body (json/generate-string {:symbol symbol
                                                  :quantidade qtd
                                                  :data data})}))

(defn parseTransacao [response]
  (let [data (get-in response [:body :data])
        acao (get-in response [:body :acao])
        quantidade (get-in response [:body :quantidade])
        preco-unitario (get-in response [:body :preco-unitario])
        total (get-in response [:body :total])]
    [data acao quantidade preco-unitario total]))

(defn requestSaldo []
  (http-client/get (str APIBASEURL "/saldo")))

(defn parseSaldo [response]
  (let [corpo (get-in response [:body])
        mapa (json/parse-string corpo true)]
    mapa))

(defn requestExtrato [inicio fim]
  (http-client/post (str APIBASEURL "/extrato")
                    {:headers {"Content-Type" "application/json"}
                     :as :json
                     :body (json/generate-string {:inicio inicio
                                                  :fim fim})}))



;;Menus
(defn consultarAcao-MENU []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "=+= Digite a acao que deseja consultar: =+=")
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+\n")
  (let [symbol (read)
        vetorRetorno (parseConsultaAcao (requestConsultaAcao symbol))]
    (imprimirConsulta vetorRetorno)))

(defn comprarAcao-MENU []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")
  (println "Digite a acao que deseja comprar: ")
  (let [acao (read)
        _ (println "Digite a quantidade: ")
        qtd (read)
        __ (println "Digite a data (yyyy-mm-dd): ")
        ___ (read-line)
        data (read-line)]
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")

    (let [resp (requestCompraAcao acao qtd data)]
      (if (respostaValida? resp)
        (imprimirCompra (parseTransacao resp))
        (println "Erro ao realizar compra:" (:body resp))))))


(defn venderAcao-MENU []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")
  (println "Digite a acao que deseja vender: ")

  (let [acao (read)
        _ (println "Digite a quantidade: ")
        qtd (read)
        __ (println "Digite a data (yyyy-mm-dd): ")
        ___ (read-line)
        data (read-line)]
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")

    (let [resp (requestVendeAcao acao qtd data)]
      (if (respostaValida? resp)
        (imprimirVenda (parseTransacao resp))
        (println "Erro ao realizar venda:" (:body resp))))))


(defn exibirExtrato-MENU []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")
  (println "=+= Para qual periodo deseja verificar o extrato?: =+=")
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")

  (println "Digite a data de inicio (yyyy-mm-dd): ")
  (let [___ (read-line)
        inicio (read-line)
        _ (println "Digite a data de fim (yyyy-mm-dd): ")
        fim (read-line)]
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
    (imprimirExtrato
     (requestExtrato inicio fim))))


(defn menu []
  (println "\n\n+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
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
      (= opcao 1) (consultarAcao-MENU)
      (= opcao 2) (comprarAcao-MENU)
      (= opcao 3) (venderAcao-MENU)
      (= opcao 4) (exibirExtrato-MENU)
      (= opcao 5) (imprimirSaldo
                   (parseSaldo
                    (requestSaldo)))
      :else (println "..."))
    (recur)))

(defn -main
  [& args]
  (menu))
