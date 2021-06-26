package academy.mindswap;

/**
 * ShipType enum which contains all the different types of ship which can be placed and respective length,
 * along with ShipType constructor, and ship name and ship length getters.
 */
public enum ShipType {
    DESTROYER("Destroyer", 2),
    SUBMARINE("Submarine", 3),
    BATTLESHIP("Battleship", 4),
    CARRIER("Carrier", 5);

    private final String shipName;
    private final int shipLength;

    /**
     * ShipType constructor.
     * @param shipName name of the ship
     * @param shipLength length of the ship
     */
    ShipType(String shipName, int shipLength) {
        this.shipName = shipName;
        this.shipLength = shipLength;
    }

    /**
     * Ship name getter.
     * @return ship name
     */
    public String getShipName() {
        return shipName;
    }

    /**
     * Ship length getter.
      * @return ship length
     */
    public int getShipLength() {
        return shipLength;
    }
}
