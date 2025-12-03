(ns carteirafront.core
  (:require
   [cheshire.core :as json]
   [clj-http.client :as http-client])
  (:gen-class))



;;Variaveis
(def APIBASEURL "http://localhost:3000") ;;mudar para .env

;;Common
(defn respostaValida? [response]
  (and
   (some? response)

   (map? response)
   (some? (:body response))

   (not (string? (:body response)))

   (not (contains? (:body response) :mensagem))
   (not= "erro" (:status (:body response)))))

(defn imprimirConsulta [vetor]
  (println "\n+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "Acao:"     (nth vetor 0))
  (println "Abertura:" (nth vetor 1))
  (println "Alta:"     (nth vetor 2))
  (println "Baixa:"    (nth vetor 3))
  (println "Preco:"    (nth vetor 4))
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
(defn consultarAcao [symbol]
  (let [url (str APIBASEURL "/acao/" symbol)
        response (http-client/get url {:as :json})

        acao (get-in response [:body :acao])
        abertura (get-in response [:body :abertura])
        alta (get-in response [:body :alta])
        baixa (get-in response [:body :baixa])
        preco (get-in response [:body :preco])]
    [acao abertura alta baixa preco]))


(defn comprarAcao [symbol qtd data]
  (println "comprando " qtd " de " symbol " no dia " data "...")
  (http-client/post (str APIBASEURL "/compra")
                    {:headers {"Content-Type" "application/json"}
                     :as :json
                     :body (json/generate-string {:symbol symbol
                                                  :quantidade qtd
                                                  :data data})}))

(defn venderAcao [symbol qtd data]
  (println "vendendo " qtd " de " symbol " no dia " data "...")
  (http-client/post (str APIBASEURL "/vende")
                    {:headers {"Content-Type" "application/json"}
                     :as :json
                     :body (json/generate-string {:symbol symbol
                                                  :quantidade qtd
                                                  :data data})}))

(defn processaTransacao [json]
  (let [response json 
        data (get-in response [:body :data])
        acao (get-in response [:body :acao])
        quantidade (get-in response [:body :quantidade])
        preco-unitario (get-in response [:body :preco-unitario])
        total (get-in response [:body :total])]
    [data acao quantidade preco-unitario total]))

(defn processaExtrato [json]
  (let [response json
         str (get-in response [:body ])]

     str))
 
(defn processaSaldo [json]
  (let [response json
        str (get-in response [:body])]
    str))

(defn exibirExtrato [inicio fim]
   (http-client/post (str APIBASEURL "/extrato")
                     {:headers {"Content-Type" "application/json"}
                      :as :json
                      :body (json/generate-string {:inicio inicio
                                                  :fim fim})}))

(defn exibirSaldo []
  (http-client/get (str APIBASEURL "/saldo")))

;;Menus
(defn consultarAcao-MENU []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "=+= Digite a acao que deseja consultar: =+=")
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+\n")
  (let [resp (consultarAcao (read))]
      (if (respostaValida? resp)
        (imprimirConsulta resp)
        (println "Erro ao realizar consulta: " (:body resp)))))

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

    (let [resp (comprarAcao acao qtd data)]
      (if (respostaValida? resp)
        (imprimirCompra (processaTransacao resp))
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
    
    (let [resp (venderAcao acao qtd data)]
      (if (respostaValida? resp)
        (imprimirVenda (processaTransacao resp))
        (println "Erro ao realizar venda:" (:body resp))))))


(defn exibirExtrato-MENU []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")
  (println "=+= Para qual periodo deseja verificar o extrato?: =+=")
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=")
  
  (println "Digite o inicio: ")
  (let [inicio (read)
        _ (println "Digite o fim: ")
        final (read)]
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
    (println
     (processaExtrato
      (exibirExtrato inicio final)))))


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
      (= opcao 1) (consultarAcao-MENU)
      (= opcao 2) (comprarAcao-MENU)
      (= opcao 3) (venderAcao-MENU)
      (= opcao 4) (exibirExtrato-MENU)
      (= opcao 5) (println(processaSaldo(exibirSaldo)))
      :else (println "..."))
    (recur)))

(defn -main
  [& args]
  (menu))
