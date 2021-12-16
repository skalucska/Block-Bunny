package com.neet.blockbunny.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class MyContactListerner implements ContactListener{
	
	private int numFootContacts;
	private Array<Body> bodiesToRemove;
	//private boolean playerOnGround;

	public MyContactListerner() {
		
		super();
		
		bodiesToRemove = new Array<Body>();
	}
    // called when two fixtures start to collide
    public void beginContact(Contact c) {
        // System.out.println("Begin Contact");

        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if(fa == null || fb == null) return;
        
        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            //playerOnGround = true;
        	numFootContacts++;
        }

        if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
            //playerOnGround = true;
            numFootContacts++;
        }
        
        if(fa.getUserData() != null && fa.getUserData().equals("crystal")) {
        	// remove crystal
        	bodiesToRemove.add(fa.getBody());
        }
        if(fb.getUserData() != null && fb.getUserData().equals("crystal")) {
        	// remove crystal
        	bodiesToRemove.add(fb.getBody());
        }

    }

    public void endContact(Contact c) {
        // System.out.println("End Contact");

        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if(fa == null || fb == null) return;
        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            //playerOnGround = false;
        	numFootContacts--;
        }

        if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
            //playerOnGround = false;
        	numFootContacts--;
        }
    }

    public boolean isPlayerOnGround() {
        //return playerOnGround;
    	return numFootContacts > 0;
    }
    public Array<Body> getBodiesToRemove(){ return bodiesToRemove; }

    // collision detection
    // collision handling
    public void preSolve(Contact c, Manifold m) {
    }

    public void postSolve(Contact c, ContactImpulse ci) {

    }
}
