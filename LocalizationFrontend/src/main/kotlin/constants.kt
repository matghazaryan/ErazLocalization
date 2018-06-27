interface Constants {
    class PATH {
        companion object {
            val CREATE_PROJECT = "create-project"
        }
    }
    class FIREBASE {
        companion object {
            val PROJECT_ID = "localization-1be56"
            val FORMAT_JSON = ".json"
        }
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
    class CREATE_PROJECT {
        companion object {
            val NAME = "name"
            val ALIAS = "alias"
            val BASE_LANG = "base_lang"
            val OPT_LANG = "opt_lang"
        }
    }
}