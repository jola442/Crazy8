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
                return "ACE";
            }

            else if(value == 11){
                return "JACK";
            }

            else if(value == 12){
                return "QUEEN";
            }

            else if(value == 13){
                return "KING";
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

    public enum Direction{
        LEFT,
        RIGHT
    }

    public static final int NUM_CARDS = 52;
    public static final int NUM_STARTING_CARDS = 5;
    public static final int MAX_NUM_DRAWS_PER_TURN = 3;
}
