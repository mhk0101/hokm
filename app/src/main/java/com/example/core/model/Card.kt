package com.example.core.model

enum class Suit(val symbol: String, val persianName: String, val isRed: Boolean) {
    SPADES("♠", "پیک", false),
    HEARTS("♥", "دل", true),
    DIAMONDS("♦", "خشت", true),
    CLUBS("♣", "گشنیز", false);

    companion object {
        fun fromSymbol(symbol: String): Suit? = entries.find { it.symbol == symbol || it.name == symbol }
    }
}

enum class Rank(val value: Int, val display: String, val persianName: String) {
    TWO(2, "2", "۲"),
    THREE(3, "3", "۳"),
    FOUR(4, "4", "۴"),
    FIVE(5, "5", "۵"),
    SIX(6, "6", "۶"),
    SEVEN(7, "7", "۷"),
    EIGHT(8, "8", "۸"),
    NINE(9, "9", "۹"),
    TEN(10, "10", "۱۰"),
    JACK(11, "J", "سرباز"),
    QUEEN(12, "Q", "بی‌بی"),
    KING(13, "K", "شاه"),
    ACE(14, "A", "تک");

    companion object {
        fun fromValue(value: Int): Rank? = entries.find { it.value == value }
    }
}

data class Card(
    val suit: Suit,
    val rank: Rank
) : Comparable<Card> {
    val id: String get() = "${suit.name}_${rank.name}"

    override fun compareTo(other: Card): Int {
        return if (this.suit == other.suit) {
            this.rank.value.compareTo(other.rank.value)
        } else {
            this.suit.ordinal.compareTo(other.suit.ordinal)
        }
    }

    override fun toString(): String = "${rank.display}${suit.symbol}"

    companion object {
        fun createDeck(): List<Card> {
            val deck = ArrayList<Card>(52)
            for (suit in Suit.entries) {
                for (rank in Rank.entries) {
                    deck.add(Card(suit, rank))
                }
            }
            return deck
        }

        fun fromId(id: String): Card? {
            val parts = id.split("_")
            if (parts.size != 2) return null
            val suit = runCatching { Suit.valueOf(parts[0]) }.getOrNull() ?: return null
            val rank = runCatching { Rank.valueOf(parts[1]) }.getOrNull() ?: return null
            return Card(suit, rank)
        }
    }
}
