(ns cljs-node-webkit-examples.editor
  (:use [domina :only [by-id]]
        [domina.events :only [listen! dispatch! prevent-default]]))

(def gui (js/require "nw.gui"))
(def fs (js/require "fs"))
(def clipboard (.get (.-Clipboard gui)))

(def editor (atom nil))
(def file-entry (atom nil))
(def has-write-access (atom nil))

;; document change functions --------------------------------------------------
(defn set-title
  [title]
  (if title
    (let [title (nth (.match title #"[^/]+$") 0)]
      (aset (by-id "title") "innerHTML" title)
      (aset js/document "title" title))
    (aset (by-id "title") "innerHTML" "[no document loaded")))

(defn get-mode
  [title]
  (when title
    (cond 
      (.match title #".json$")
      [(clj->js {"name" "javascript" "json" true}) "Javascript (JSON)"]
      (.match title #".html$")
      ["htmlmixed" "HTML"]
      (.match title #".css$")
      ["css" "CSS"])))

(defn handle-document-change
  [title]
  (let [title (set-title title)
        [mode mode-name] (get-mode title)]
    (.setOption @editor "mode" mode)
    (aset (by-id "mode") "innerHTML" mode-name)))

;; file functions -------------------------------------------------------------
(defn on-chosen-file-to-save [fileEntry]
  (.log js/console "save file"))

(defn on-chosen-file-to-open [file-entry]
  (.log js/console "deal with file"))

(defn new-file []
  (reset! file-entry nil)
  (reset! has-write-access nil)
  (handle-document-change nil))


;; button click handlers ------------------------------------------------------
(defn handle-new-button []
  (.log js/console "clicked new"))

(defn handle-open-button []
  (.click (by-id "openFile")))

(defn handle-save-button []
  (.log js/console "clicked save"))


;; context-menu ---------------------------------------------------------------
(defn create-item
  [args]
  (let [menu-item (.-MenuItem gui)]
    (new menu-item (clj->js args))))

(defn copy-func []
  (.set clipboard (.getSelection @editor)))

(defn cut-func []
  (do
    (.set clipboard (.getSelection @editor))
    (.replaceSelection @editor "")))

(defn paste-func []
  (.replaceSelection @editor (.get clipboard)))

(defn contextmenu-listener 
  [event menu]
  (do
    (prevent-default event)
    (.popup menu)
    false))

(defn init-context-menu []
  (let [menu (.-Menu gui)
        copy (create-item {"label" "Copy" "click" copy-func})
        cut (create-item {"label" "Cut" "click" cut-func})
        paste (create-item {"label" "Paste" "click" paste-func})
        context-menu (doto (new menu)
                       (.append copy)
                       (.append cut)
                       (.append paste))]
    (listen! (by-id "editor") :contextmenu 
             #(contextmenu-listener % context-menu))))


;; init -----------------------------------------------------------------------
(defn new-editor []
  (js/CodeMirror (by-id "editor") 
                 (clj->js {"mode" {"name" "javascript" "json" "true"}
                                   "lineNumbers" "true"
                                   "theme" "lesser-dark"
                                   "extraKeys" {"Cmd-S" handle-save-button
                                                "Ctrl-S" handle-save-button}})))

(defn init []
  (let [new-button (by-id "new")
        open-button (by-id "open")
        save-button (by-id "save")
        open-file (by-id "openFile")
        save-file (by-id "saveFile")]
    (init-context-menu)
    (listen! new-button :click handle-new-button)
    (listen! open-button :click handle-open-button)
    (listen! save-button :click handle-save-button)
    (listen! open-file :change #(on-chosen-file-to-open open-file))
    (listen! save-file :change #(on-chosen-file-to-save save-file))
    (reset! editor (new-editor))
    (new-file)))

(listen! js/window :load init)