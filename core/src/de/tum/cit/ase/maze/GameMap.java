package de.tum.cit.ase.maze;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GameMap {
    private MapObject[][] staticMapObjects;
    private List<Enemy> enemies;
    private int keysLeft;

    private List<EntryPoint> entryPoints;

    public GameMap() {
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    // Loads Game Map into the staticMapObjects
    public void loadMap(String filePath) {
        enemies = new ArrayList<>();
        Properties prop = new Properties();
        int[][] intArray;
        int maxRow = 0, maxCol = 0;
        keysLeft = 0;
        try (InputStream input = new FileInputStream(filePath)) {
            prop.load(input);

            // First pass to find the size of the array
            for (String key : prop.stringPropertyNames()) {
                String[] parts = key.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                maxRow = Math.max(maxRow, row);
                maxCol = Math.max(maxCol, col);
            }

            // Initialize the intArray with -1
            intArray = new int[maxRow + 1][maxCol + 1]; // +1 because arrays are 0-indexed
            for (int[] row : intArray) {
                Arrays.fill(row, -1);
            }

            // Second pass to fill the array
            for (String key : prop.stringPropertyNames()) {
                String[] parts = key.split(",");
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                intArray[row][col] = Integer.parseInt(prop.getProperty(key));
            }
            // Example to print the array
            for (int i = 0; i < intArray.length; i++) {
                for (int j = 0; j < intArray[i].length; j++) {
                    System.out.print(intArray[i][j] + " ");
                }
                System.out.println();
            }

            staticMapObjects = new MapObject[maxRow + 1][maxCol + 1];
            entryPoints = new ArrayList<>();

            for (int i = 0; i < intArray.length; i++) {
                for (int j = 0; j < intArray[i].length; j++) {
                    switch (intArray[i][j]) {
                        case 0:
                            staticMapObjects[i][j] = new Wall();
                            break;
                        case 1:
                            staticMapObjects[i][j] = new EntryPoint();
                            entryPoints.add((EntryPoint) staticMapObjects[i][j]);
                            ((EntryPoint) staticMapObjects[i][j]).setX(j);
                            ((EntryPoint) staticMapObjects[i][j]).setY(i);
                            break;
                        case 2:
                            staticMapObjects[i][j] = new Exit();
                            break;
                        case 3:
                            staticMapObjects[i][j] = new Trap();
                            break;
                        case 4:
                            staticMapObjects[i][j] = new Path();
                            enemies.add(new Enemy(i, j));
                            enemies.get(enemies.size() - 1).setWindowCordsFromTilet(GameScreen.tileSize);
                            break;
                        case 5:
                            staticMapObjects[i][j] = new Key();
                            keysLeft++;
                            break;
                        default:
                            staticMapObjects[i][j] = new Path();
                            break;
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public MapObject[][] getStaticMapObjects() {
        return staticMapObjects;
    }

    public float getMapWidth() {
        return staticMapObjects[0].length;
    }

    public float getMapHeight() {
        return staticMapObjects.length;
    }


    public MapObject getTile(float x, float y, float tileSize) {
        int i = (int) ((x) / tileSize);
        int j = (int) ((y) / tileSize);
        return getStaticMapObjects()[j][i];
    }

    public int getKeysLeft() {
        return keysLeft;
    }

    public void removeKey(float x, float y, float tileSize) {
        int i = (int) ((x) / tileSize);
        int j = (int) ((y) / tileSize);
        staticMapObjects[j][i] = new Path();
        keysLeft--;
        if (keysLeft < 0) {
            keysLeft = 0;
        }
    }

    public List<EntryPoint> getEntryPoints() {
        return entryPoints;
    }
}
