package com.crazy8.game;

public class Defs {
    public enum Rank {
        ACE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13);

        private final int value;

        Rank(int value){
            this.value = value;
        }

        @Override
        public String toString() {
            if(value == 1){
                return "ace";
            }

            else if(value == 11){
                return "jack";
            }

            else if(value == 12){
                return "queen";
            }

            else if(value == 13){
                return "king";
            }

            else{
                return Integer.toString(value);
            }

        }
    }

    public enum Suit {
        CLUBS,
        SPADES,
        HEARTS,
        DIAMONDS;
    }

    public static final int NUM_CARDS = 52;
}
