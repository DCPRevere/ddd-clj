(ns ddd-clj.core
  (:gen-class))

;; Aggregate

(defprotocol Aggregate)

(defrecord Txn [agg-id payer payee amount events]
  Aggregate)

;; Event

(defprotocol Event)

(defrecord TxnCreated [agg-id data]
  Event)

(defrecord TxnAmountChanged [agg-id data]
  Event)

;; Domain repository

(defprotocol DomainRepo
  (get-by-id [repo id])
  (save [repo event]))

(def eg-txns
  [{:payer "Daniel"
    :payee "Tan"
    :amount 315
    :agg-id "123"}])

(defrecord EdnStore []
  DomainRepo
  (get-by-id [repo id]
    "Rebuilds the aggregate from its events."
    (map->Txn
     (first (filter #(= id (:agg-id %)) eg-txns))))
  (save [repo agg] nil))

(defrecord EventStore []
  Domainrepo)

;; Apply event

(defmulti apply-event
  (fn [agg event]
    [(type agg) (type event)]))

(defmethod apply-event [ddd_clj.core.Txn ddd_clj.core.TxnAmountChanged]
  [agg event]
  (assoc agg :amount (:amount (:data event))))

;; Commands and handler

(defprotocol Command
  (handle [command repo]))

(defrecord ChangeTxnAmount [agg-id amount]
  Command
  (handle [command repo]
    (let [agg-id (:agg-id command)
          amount (:amount command)
          txn (get-by-id repo agg-id)
          events [(->TxnAmountChanged agg-id {:amount amount})]]
      (map #(save repo %) events)
      (reduce apply-event txn events))))

(handle (->ChangeTxnAmount "123" 263) (->EdnStore))
(handle (->ChangeTxnAmount "123" 433) (->EdnStore))

(reduce apply-event
        (get-by-id (->EdnStore) "123")
        [(->TxnAmountChanged "123" {:amount 343})
         (->TxnAmountChanged "123" {:amount 765})
         (->TxnAmountChanged "123" {:amount 34})])
