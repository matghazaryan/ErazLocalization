interface Constants {
    class FIREBASE {
        interface contentType {
            companion object {
                val VALUE = "value"
                val CHILD_ADDED = "child_added"
                val CHILD_CHANGED = "child_changed"
                val CHILD_REMOVED = "child_removed"
                val CHILD_MOVED = "child_moved"
            }
        }
    }
    class YANDEX {
        companion object {
            val KEY = "trnsl.1.1.20180629T054817Z.31806ab54c4a97c8.96387b071cabbf73cd2338577c1c19060001fe66"
            val DETECTION_URL = "https://translate.yandex.net/api/v1.5/tr.json/detect"
            val TRANSLATE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate"
            val LANGUAGES_URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs"
        }
    }
}