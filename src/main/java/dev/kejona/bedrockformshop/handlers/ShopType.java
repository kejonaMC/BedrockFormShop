package dev.kejona.bedrockformshop.handlers;

public enum ShopType {

    ITEM {
        @Override
        public String toString() {
            return "ITEM";
        }
    },
    COMMAND {
        @Override
        public String toString() {
            return "COMMAND";
        }
    },
}
