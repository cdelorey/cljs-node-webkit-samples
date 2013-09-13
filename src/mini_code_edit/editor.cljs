(ns cljs-node-webkit-examples.editor
  (:use [domina :only [by-id set-style! value]]
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
(defn write-editor-to-file
  [file]
  (let [data (.getValue @editor)]
    (.log js/console (str "file-entry: " file))
    (.writeFile fs file data 
                #(if % 
                   (.log js/console (str "Write failed:" %))
                   (do
                     (handle-document-change file)
                     (.log js/console "Write completed."))))))

(defn set-file 
  [file writeable?]
  (do
    (reset! file-entry file)
    (reset! has-write-access writeable?)))

(defn on-chosen-file-to-save 
  [file]
  (do
    (set-file file true)
    (write-editor-to-file file)))

(defn on-chosen-file-to-open 
  [file-entry]
  (.log js/console "deal with file"))

(defn new-file []
  (reset! file-entry nil)
  (reset! has-write-access nil)
  (handle-document-change nil))


;; button click handlers ------------------------------------------------------
(defn handle-new-button []
  (if false
    (do
      (new-file)
      (.setValue @editor ""))
    (let [x (+ 10 (.-screenX js/window))
          y (+ 10 (.-screenY js/window))]
      (.open js/window "main.html" "_blank" 
             (str "screenX=" x) (str "screenY=") y))))
 
(defn handle-open-button []
  (.click (by-id "openFile")))

(defn handle-save-button []
  (if (and @file-entry @has-write-access)
    (write-editor-to-file @file-entry)
    (.click (by-id "saveFile"))))


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
(defn on-resize []
  (let [container (by-id "editor")
        container-width (aget container "offsetWidth")
        container-height (aget container "offsetHeight")
        scroller-element (.getScrollerElement @editor)]
    (set-style! scroller-element "width" (str container-width "px"))
    (set-style! scroller-element "height" (str container-height "px"))
    (.refresh @editor)))

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
    (listen! open-file :change #(on-chosen-file-to-open (value open-file)))
    (listen! save-file :change #(on-chosen-file-to-save (value save-file)))
    (reset! editor (new-editor))
    (new-file)
    (on-resize)))

(listen! js/window :load init)