package model.cards;

import model.Board;
import model.Card;
import model.Player;
import model.Property;

import java.util.List;

public class MoveToOpponentBestPropertyCard extends Card {
    private final List<Player> allPlayers;

    public MoveToOpponentBestPropertyCard(String description, List<Player> players) {
        super(description);
        this.allPlayers = players;
    }

    @Override
    public void execute(Player currentPlayer, Board board) {
        System.out.println(">> Card Action: " + getDescription());

        // Logic หาคู่แข่งที่แข็งแกร่งที่สุด
        Player opponent = allPlayers.stream()
                .filter(p -> p != currentPlayer && p.getMoney() > 0) // กรองผู้เล่นที่ไม่ใช่ตัวเองและยังไม่ล้มละลาย
                .max((p1, p2) -> Integer.compare(p1.getMoney(), p2.getMoney()))
                .orElse(null);

        if (opponent != null) {
            // Logic หา Property ที่แพงที่สุดของคู่แข่ง
            Property targetProperty = board.getSquares().stream()
                    .filter(s -> s instanceof Property)
                    .map(s -> (Property) s)
                    .filter(p -> p.getOwner() == opponent)
                    .max((p1, p2) -> Integer.compare(p1.getPrice(), p2.getPrice()))
                    .orElse(null);

            if (targetProperty != null) {
                System.out.println(">> Warping to " + opponent.getName() + "'s property: " + targetProperty.getName());
                // ใช้ warp() เพื่อกระโดดไปเลย (ไม่ผ่าน Go)
                currentPlayer.warp(targetProperty.getPosition());

                // กระตุ้น Logic เมื่อลงบนช่องนั้น
                board.getSquare(currentPlayer.getPosition()).landOn(currentPlayer);
            } else {
                System.out.println(">> Opponent has no owned property to jump to.");
            }
        }
    }
}