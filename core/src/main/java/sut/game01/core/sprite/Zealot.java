package sut.game01.core.sprite;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import debug.DebugDrawBox2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import playn.core.Layer;
import playn.core.PlayN;
import playn.core.Pointer;
import playn.core.util.Callback;
import playn.core.util.Clock;
import sut.game01.core.TestScreen;


public class Zealot {


    private Sprite sprite;
    private int spriteIndex = 0;
    private boolean hasLoaded = false;
    private int hp = 100;
    private Body body;


    public Layer layer() {
        return sprite.layer();
    }

    public void paint(Clock clock) {
        if(!hasLoaded)return;
        sprite.layer().setTranslation(body.getPosition().x/TestScreen.M_PER_PIXEL-10, body.getPosition().y/TestScreen.M_PER_PIXEL);
    }

    public Body body() {
        return body;
    }


    public enum State {
        IDLE, RUN, DIE, ATTK
    };

    private State state = State.IDLE;

    private int e = 0;
    private int offset = 0;


    public Zealot(final World world, final float x_px, final float y_px) {
        this.sprite = SpriteLoader.getSprite("images/zealot.json");
        sprite.addCallback(new Callback<Sprite>() {
            @Override
            public void onSuccess(Sprite result) {
                sprite.setSprite(spriteIndex);
                sprite.layer().setOrigin(sprite.width() / 2f,
                        sprite.height() / 2f);
                sprite.layer().setTranslation(x_px , y_px);
                body = initPhysicsBody(world, TestScreen.M_PER_PIXEL * x_px, TestScreen.M_PER_PIXEL * y_px);


                hasLoaded = true;
            }

            @Override
            public void onFailure(Throwable cause) {
                PlayN.log().error("Error loading image!", cause);
            }
        });

        sprite.layer().addListener(new Pointer.Adapter(){
            @Override
            public void onPointerEnd(Pointer.Event event) {
                state = State.ATTK;
                spriteIndex = -1;
                e = 0;
            }

        });

    }

    private Body initPhysicsBody(World world, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position = new Vec2(0, 0);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(56*TestScreen.M_PER_PIXEL / 2, sprite.layer().height()*TestScreen.M_PER_PIXEL / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.4f;
        fixtureDef.friction = 0.1f;
        body.createFixture(fixtureDef);

        body.setLinearDamping(0.2f);
        body.setTransform(new Vec2(x, y), 0f);
        return body;
    }

    public void contact(Contact contact){
        boolean contacted = true;
        int contactCheck = 0;

            if(state == State.DIE){
                state = State.ATTK;
            }
            if(contact.getFixtureA().getBody() == body){
                Body other = contact.getFixtureB().getBody();
            }else{
                Body other = contact.getFixtureA().getBody();
            }
        }



    public void update(int delta) {
        if (!hasLoaded) return;
        e += delta;

        if (e > 150) {
            switch (state) {
                case IDLE: offset = 0;
                    break;
                case ATTK: offset = 5;
                    break;
                case DIE: offset = 10;
                    break;

            }

            spriteIndex = offset + ((spriteIndex + 1) % 4);
            sprite.setSprite(spriteIndex);
            e = 0;

            hp -= 50;
            e=0;
            if(hp == 0){
                state = State.DIE;
            }
            if(hp < 0){
                spriteIndex = 14;
            }

        }
    }

}

