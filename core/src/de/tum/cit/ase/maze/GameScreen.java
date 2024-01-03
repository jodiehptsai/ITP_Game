package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;


import static de.tum.cit.ase.maze.Direction.*;


import com.badlogic.gdx.ScreenAdapter;
//Test
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen extends ScreenAdapter implements Screen, Serializable {
    //JODIE TRY
    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;

    int gameState;

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    //JODIE TRY

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;


    private float sinusInput = 0f;

    private final float tileSize = 80;


    private float playerSpeed = 3;

    private  float mapMaxX, mapMaxY;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;

        gameState = GAME_RUNNING; // JODIE TRY

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        //Create Player
        game.getGameEngine().getPlayer().setCurrentWindowX(camera.viewportWidth / 2);
        game.getGameEngine().getPlayer().setCurrentWindowY(camera.viewportHeight / 2);
        game.setGameScreen(this);



    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //this.gameState=GAME_PAUSED;
            //sound effect
            Music escMusic = Gdx.audio.newMusic(Gdx.files.internal("ESC_sound.mp3"));
            escMusic.setVolume(2.5f);
            escMusic.setLooping(false);
            escMusic.play();

            //go to pause menu

            game.goToPauseMenu();

        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            //Sound effect
            Music escMusic = Gdx.audio.newMusic(Gdx.files.internal("ESC_sound.mp3"));
            escMusic.setVolume(2.5f);
            escMusic.setLooping(false);
            escMusic.play();
            //go to Victory Screen
            game.goToVictoryScreen();

        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            //Sound effect
            Music escMusic = Gdx.audio.newMusic(Gdx.files.internal("ESC_sound.mp3"));
            escMusic.setVolume(2.5f);
            escMusic.setLooping(false);
            escMusic.play();
            //go to Victory Screen
            game.goToGameOverScreen();

        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        if(gameState==GAME_RUNNING) {
            renderMap();
            Player player = game.getGameEngine().getPlayer();

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                player.setDirection(UP);

                    renderPlayer();

            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                player.setDirection(DOWN);
                renderPlayer();
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.setDirection(LEFT);
                renderPlayer();
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                player.setDirection(RIGHT);
                renderPlayer();
            } else {
                // Do not change the direction here, just render the standing player
                renderStandingPlayer();
            }


            if (isPlayerAtEdge()) {
                System.out.println("Player at Edge; Updating Camera");
                updateCameraPosition();
            }

            //camera.update(); // Update the camera

            // Move text in a circular path to have an example of a moving object
            sinusInput += delta;
            float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
            float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);

            // Set up and begin drawing with the sprite batch
            game.getSpriteBatch().setProjectionMatrix(camera.combined);


            game.getSpriteBatch().begin(); // Important to call this before drawing anything

            game.getSpriteBatch().end(); // Important to call this after drawing everything


            camera.update(); // Update the camera


            // Move text in a circular path to have an example of a moving object
            sinusInput += delta;
            //float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
            //float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);

            // Set up and begin drawing with the sprite batch
            game.getSpriteBatch().setProjectionMatrix(camera.combined);


            game.getSpriteBatch().begin(); // Important to call this before drawing anything

            game.getSpriteBatch().end(); // Important to call this after drawing everything

            player.setCurrentTileFromCoords(game.getGameEngine().getStaticGameMap(), tileSize);

            System.out.println("Tile: "+ player.getCurrentTile());
        }
    }

    // Render Map
    public void renderMap() {
        MapObject[][] mapObjects = game.getGameEngine().getStaticGameMap().getStaticMapObjects();

        // Calculate the size of each tile on the screen, for example:
        game.getSpriteBatch().begin();

        for (int row = 0; row < mapObjects.length; row++) {
            for (int col = 0; col < mapObjects[row].length; col++) {
                if (mapObjects[row][col] != null) {
                    float x = col * tileSize;
                    float y = row * tileSize;
                    mapObjects[row][col].render(game.getSpriteBatch(), x, y, tileSize);
                }
            }
        }

        mapMaxX = mapObjects.length * tileSize;
        mapMaxY = mapObjects[0].length * tileSize;

        game.getSpriteBatch().end();
    }

    private void renderStandingPlayer() {
        Player player = game.getGameEngine().getPlayer();
        float x = player.getCurrentWindowX();
        float y = player.getCurrentWindowY();

        TextureRegion currentFrame = null;

        switch (player.getDirection()) {
            case STANDINGUP:
                currentFrame = player.getCharacterStandingUpTexture();
                break;
            case STANDINGDOWN:
                currentFrame = player.getCharacterStandingDownTexture();
                break;
            case STANDINGLEFT:
                currentFrame = player.getCharacterStandingLeftTexture();
                break;
            case STANDINGRIGHT:
                currentFrame = player.getCharacterStandingRightTexture();
                break;
            default:
                renderPlayer();
                return;
        }

        game.getSpriteBatch().begin();
        // Draw the standing frame scaled to tileSize
        game.getSpriteBatch().draw(
                currentFrame,
                x, y,
                64, 128 // Adjust the size as needed
        );
        game.getSpriteBatch().end();
    }
    private void renderPlayer() {
        float projectionPlaneHeight = camera.viewportHeight / camera.zoom;
        float projectionPlaneWidth = game.getGameEngine().getStaticGameMap().getMapWidth()*tileSize;//camera.viewportWidth / camera.zoom;
        Player player = game.getGameEngine().getPlayer();
        player.move(playerSpeed);
        float x = player.getCurrentWindowX();
        float y = player.getCurrentWindowY();

        if(x < 0){
            player.setCurrentWindowX(0);
        } else if (y < 0 ) {
            player.setCurrentWindowY(0);
        } else if (x+tileSize > projectionPlaneWidth) {
            System.out.println("Player at Right Part of the Map "+ player.getCurrentWindowX()+" "+player.getCurrentWindowY()+ " proejctionPlanteWidth: "+projectionPlaneWidth);
            player.setCurrentWindowX(projectionPlaneWidth-tileSize);
        } else if (y-tileSize > projectionPlaneHeight) {
            System.out.println("Player at Top Part of the Map "+ player.getCurrentWindowX()+" "+player.getCurrentWindowY()+ " proejctionPlanteHeight: "+projectionPlaneHeight);
            player.setCurrentWindowY(projectionPlaneHeight+tileSize);
        }

        Animation<TextureRegion> anim = null;

        switch (player.getDirection()) {
            case UP -> anim = player.getCharacterUpAnimation();
            case DOWN -> anim = player.getCharacterDownAnimation();
            case LEFT -> anim = player.getCharacterLeftAnimation();
            case RIGHT -> anim = player.getCharacterRightAnimation();
        }

        TextureRegion currentFrame = anim.getKeyFrame(sinusInput, true);

        game.getSpriteBatch().begin();
        game.getSpriteBatch().draw(currentFrame, x, y, 64, 128);
        game.getSpriteBatch().end();

        player.setDirection(getStandingDirection(player.getDirection()));
    }
    private boolean isPlayerAtBarrier(){
        Player player = game.getGameEngine().getPlayer();
        float offsetVertical= 0;
        float offsetHorizontal = 20;

/*
        System.out.println("Player Direction: " + player.getDirection());
        System.out.println("Player CurrentWindowX: " + player.getCurrentWindowX());
        System.out.println("Player CurrentWindowY: " + player.getCurrentWindowY());
        System.out.println("Offset Vertical: " + offsetVertical);
        System.out.println("Offset Horizontal: " + offsetHorizontal);
        System.out.println("Tile Size: " + tileSize);
        System.out.println("MapMaxX: "+mapMaxX);
        System.out.println("MapMaxY: "+mapMaxY);
        */

        switch (player.getDirection()) {
            case LEFT -> {
                if(game.getGameEngine().getStaticGameMap().getTile(player.getCurrentWindowX(), player.getCurrentWindowY(), tileSize) instanceof Wall){
                    return true;
                }
            }
        }
        return false;
    }

    private Direction getStandingDirection(Direction currentDirection) {
        return switch (currentDirection) {
            case UP -> STANDINGUP;
            case DOWN -> STANDINGDOWN;
            case LEFT -> STANDINGLEFT;
            case RIGHT -> STANDINGRIGHT;
            // For standing directions, keep the same
            default -> currentDirection;
        };
    }

    private boolean isPlayerAtEdge() {
        Player player = game.getGameEngine().getPlayer();
        float playerX = player.getCurrentWindowX();
        float playerY = player.getCurrentWindowY();

        float edge = 0.2f;

        //.out.println("Camera Coords x/y:"+camera.position.x+ " "+ camera.position.y);
        //System.out.println("Camera Viewport w/h"+camera.viewportWidth +" "+camera.viewportHeight);
        //System.out.println("PlayerCoords x/y "+playerX + " "+playerY);

        //System.out.println("Left Bound "+(camera.position.x - 0.5 * camera.viewportWidth));
        //System.out.println("Right Bound "+(camera.position.x + 0.5 * camera.viewportWidth));
        // Check left and right bounds
        if (playerX < camera.position.x - 0.5 * camera.viewportWidth + tileSize + tileSize) {
            return true;
        } else if (playerX > camera.position.x + 0.5 * camera.viewportWidth- tileSize-tileSize) {
            return true;
        }

        //System.out.println("Top Bound "+(camera.position.y + 0.5 * camera.viewportHeight));
        //System.out.println("Bottom Bound "+(camera.position.y - 0.5 * camera.viewportHeight));

        // Check top and bottom bounds
        if (playerY < camera.position.y - 0.5 * camera.viewportHeight+ tileSize+tileSize)  {
            return true;
        } else if (playerY  > camera.position.y + 0.5 * camera.viewportHeight - tileSize- tileSize) {
            return true;
        }

        return false;
    }

    private void updateCameraPosition() {
        Player player = game.getGameEngine().getPlayer();
        float screenWidth = camera.viewportWidth;
        float screenHeight = camera.viewportHeight;
        // Define how close to the edge the player must be to move the camera
        System.out.println("Screen Height: "+screenHeight);
        System.out.println("Snap");
        // Check and update camera position based on player's direction
        switch (player.getDirection()) {
            case UP:
            case STANDINGUP:
                camera.position.y += screenHeight*0.5; // Move camera up by one screen height
                break;
            case DOWN:
            case STANDINGDOWN:
                    camera.position.y -= screenHeight*0.5; // Move camera down by one screen height
                System.out.println("Set camera to: " + camera.position.y);
                break;
            case LEFT:
            case STANDINGLEFT:
                    camera.position.x -= screenWidth*0.5; // Move camera left by one screen width
                System.out.println("Set camera to: " + camera.position.x);
                break;
            case RIGHT:
            case STANDINGRIGHT:
                    camera.position.x += screenWidth*0.5; // Move camera right by one screen width
                System.out.println("Set camera to: " + camera.position.x);
                break;
        }

        float projectionPlaneHeight = camera.viewportHeight / camera.zoom;
        float projectionPlaneWidth = game.getGameEngine().getStaticGameMap().getMapWidth()*tileSize;//camera.viewportWidth / camera.zoom;

        if(camera.position.y > projectionPlaneHeight){
            camera.position.y = projectionPlaneHeight;
            System.out.println("Changed position due to camera out of map-bounds"+camera.position.y);
        }
        if(camera.position.y < 0){
            camera.position.y = 0;
            System.out.println("Changed position due to camera out of map-bounds"+camera.position.y);
        }
        if(camera.position.x < 0){
            camera.position.x = 0;
            System.out.println("Changed position due to camera out of map-bounds"+camera.position.x);
        }

        if(camera.position.x > projectionPlaneWidth){
            camera.position.x = projectionPlaneWidth;
            System.out.println("Changed position due to camera out of map-bounds"+camera.position.x);
        }
        camera.update(); // Update the camera after repositioning
    }



    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    // Additional methods and logic can be added as needed for the game screen

       /*
        // Render the text
        font.draw(game.getSpriteBatch(), "Press ESC to go to menu", textX, textY);

        // Draw the character next to the text :) / We can reuse sinusInput here
        game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                textX - 96,
                textY - 64,
                64,
                128
        );
*/
}
