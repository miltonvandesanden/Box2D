package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2D extends ApplicationAdapter implements InputProcessor {
    private SpriteBatch batch;
    private Sprite playerSprite;
    private Sprite playerSprite2;
    private Texture circle;
    private World world;
    private Body body;
    private Body body2;
    private BitmapFont font;
    
    float torque = 0.0f;
    float force = 0.0f;
    
    final float PIXELS_TO_METERS = 100f;
    
    @Override
    public void create () {
        batch = new SpriteBatch();
        
        //create a worldspace in which the object can exits
        world = new World(new Vector2(0, -1f), true);
        
        //Create a texture from an image and a sprite that is gonna be the graphical representation of the object
        circle = new Texture("badlogic.jpg");
        playerSprite = new Sprite(circle);
        
        // centering player sprite in the middle of the world
        playerSprite.setPosition(playerSprite.getWidth() / 2, playerSprite.getHeight() / 2);
                
        //Create a body for the object that is gonna be used for the physics
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        
        //set gravityScale to 0 for a top-down 
        bodyDef.gravityScale = 0;
        
        bodyDef.position.set((playerSprite.getX() + playerSprite.getWidth()/2) / 
                             PIXELS_TO_METERS, 
                (playerSprite.getY() + playerSprite.getHeight()/2) / PIXELS_TO_METERS);
        
        body = world.createBody(bodyDef);
        
        //define the shape of the object
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(playerSprite.getWidth()/2 / PIXELS_TO_METERS, playerSprite.getHeight()/2 / PIXELS_TO_METERS);
        
        //creating FixtureDef which defines different properties like density,area and mass
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        
        body.createFixture(fixtureDef);
        
        shape.dispose();
        
        playerSprite2 = new Sprite(circle);
        
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.StaticBody;
//        bodyDef2.position.set(0, 0);
        bodyDef2.position.set((playerSprite2.getX() + playerSprite2.getWidth()/2) / PIXELS_TO_METERS, (playerSprite2.getY() + playerSprite2.getHeight()/2) / PIXELS_TO_METERS);
        
        FixtureDef fixtureDef2 = new FixtureDef();

        body2 = world.createBody(bodyDef2);
        
        //define the shape of the object
        PolygonShape shape2 = new PolygonShape();
        shape2.setAsBox(playerSprite2.getWidth()/2 / PIXELS_TO_METERS, playerSprite2.getHeight()/2 / PIXELS_TO_METERS);
        
        //creating FixtureDef which defines different properties like density,area and mass
        fixtureDef2.shape = shape2;
        fixtureDef2.density = 0.1f;
        fixtureDef2.restitution = 0.5f;
        
        body2.createFixture(fixtureDef2);
        
        shape2.dispose();
        
        
        Gdx.input.setInputProcessor(this);
        
        font = new BitmapFont();
        font.setColor(Color.BLACK);
                
        //now we have a sprite for physical representation, a worldspace for the object to exist in, and a FixtureDef that describes the physical properties of the object.
    }

    private float elapsed = 0;
    
    @Override
    public void render () {
        //set the update rate of the worldspace
        world.step(1f/60f, 6, 2);
        
        //apply torque
        body.applyTorque(torque, true);
        body2.applyTorque(torque, true);
        
        
        //update the playersprite according to physics
        playerSprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - playerSprite.getWidth()/2, (body.getPosition().y * PIXELS_TO_METERS) - playerSprite.getHeight()/2);
        playerSprite.setRotation((float)Math.toDegrees(body.getAngle()));
        playerSprite2.setPosition((body2.getPosition().x * PIXELS_TO_METERS) - playerSprite2.getWidth()/2, (body2.getPosition().y * PIXELS_TO_METERS) - playerSprite2.getHeight()/2);
        playerSprite2.setRotation((float)Math.toDegrees(body2.getAngle()));
        
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        batch.draw(playerSprite, playerSprite.getX(), playerSprite.getY(), playerSprite.getOriginX(), playerSprite.getOriginY(), playerSprite.getWidth(), playerSprite.getHeight(), playerSprite.getScaleX(), playerSprite.getScaleY(), playerSprite.getRotation());
        batch.draw(playerSprite2, playerSprite2.getX(), playerSprite2.getY(), playerSprite2.getOriginX(), playerSprite2.getOriginY(), playerSprite2.getWidth(), playerSprite2.getHeight(), playerSprite2.getScaleX(), playerSprite2.getScaleY(), playerSprite2.getRotation());
        font.draw(batch, "Restitution: " + body.getFixtureList().first().getRestitution(), - Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        batch.end();
    }
    
    @Override
    public void dispose()
    {
        circle.dispose();
        world.dispose();
    }

    @Override
    public boolean keyDown(int keycode)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        if (keycode == Input.Keys.RIGHT)
        {
            body.setLinearVelocity(1f, 0f);
        }
        if (keycode == Input.Keys.LEFT)
        {
            body.setLinearVelocity(-1f, 0f);
        }
        if (keycode == Input.Keys.UP)
        {
//            body.applyForceToCenter(0f, 10f, true);
            body.setLinearVelocity(0f, 1f);
        }
        if (keycode == Input.Keys.DOWN)
        {
//            body.applyForceToCenter(0f, -10f, true);
            body.setLinearVelocity(0f, -1f);
        }
        
        // On brackets ( [ ] ) apply torque, either clock or counterclockwise
        if(keycode == Input.Keys.RIGHT_BRACKET)
        {
//            torque = 0.1f;
            body.setAngularVelocity(-1f);
        }
        if(keycode == Input.Keys.LEFT_BRACKET)
        {
            body.setAngularVelocity(1f);
        }
        
        // Remove the torque using backslash /
        if(keycode == Input.Keys.BACKSLASH)
        {
            body.setAngularVelocity(0.0f);
            body.setLinearVelocity(0.0f, 0.0f);
        }
        
        // If user hits spacebar, reset everything back to normal
        if(keycode == Input.Keys.SPACE) {
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            torque = 0f;
            playerSprite.setPosition(0f,0f);
            body.setTransform(0f,0f,0f);
        }
        
        if(keycode == Input.Keys.COMMA)
        {
             body.getFixtureList().first().setRestitution(body.getFixtureList().first().getRestitution()-0.1f);
        }
        if(keycode == Input.Keys.PERIOD)
        {
            body.getFixtureList().first().setRestitution(body.getFixtureList().first().getRestitution()+0.1f);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        if (keycode == Input.Keys.RIGHT)
        {
            body.setLinearVelocity(0f, 0f);
        }
        if (keycode == Input.Keys.LEFT)
        {
            body.setLinearVelocity(0f, 0f);
        }
        if (keycode == Input.Keys.UP)
        {
//            body.applyForceToCenter(0f, 10f, true);
            body.setLinearVelocity(0f, 0f);
        }
        if (keycode == Input.Keys.DOWN)
        {
            body.setLinearVelocity(0f, 0f);
        }
        
        // On brackets ( [ ] ) apply torque, either clock or counterclockwise
        if(keycode == Input.Keys.RIGHT_BRACKET)
        {
            body.setAngularVelocity(0f);
        }
        if(keycode == Input.Keys.LEFT_BRACKET)
        {
            body.setAngularVelocity(0f);
        }
        
//        // Remove the torque using backslash /
//        if(keycode == Input.Keys.BACKSLASH)
//        {
//            torque = 0.0f;
//        }
        
//        // If user hits spacebar, reset everything back to normal
//        if(keycode == Input.Keys.SPACE) {
//            body.setLinearVelocity(0f, 0f);
//            body.setAngularVelocity(0f);
//            torque = 0f;
//            playerSprite.setPosition(0f,0f);
//            body.setTransform(0f,0f,0f);
//        }
        
        
        return true;
    }

    @Override
    public boolean keyTyped(char character)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }
}