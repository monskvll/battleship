package academy.mindswap.ships;

public enum ShipType {
    DESTROYER("Destroyer", 2),
    SUBMARINE("Submarine", 3),
    BATTLESHIP("Battleship", 4),
    CARRIER("Carrier", 5);

    private String shipName;
    private int shipLength;

    ShipType(String shipName, int shipLength) {
        this.shipName = shipName;
        this.shipLength = shipLength;
    }

    public String getShipName() {
        return shipName;
    }

    public int getShipLength() {
        return shipLength;
    }
}
