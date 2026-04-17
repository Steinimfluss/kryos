package net.kryos.event.impl;

import net.kryos.event.Event;
import net.kryos.event.listener.impl.ModifyFieldOfViewListener;

public class ModifyFieldOfViewEvent extends Event<ModifyFieldOfViewListener> {
	public final boolean firstPerson;
    public final float effectScale;

    private boolean modified = false;
    private float newFov = 1.0F;

    public ModifyFieldOfViewEvent(boolean firstPerson, float effectScale) {
        this.firstPerson = firstPerson;
        this.effectScale = effectScale;
    }

    public void setFov(float fov) {
        this.modified = true;
        this.newFov = fov;
    }

    public boolean isModified() {
        return modified;
    }

    public float getFov() {
        return newFov;
    }

	@Override
	public void post(ModifyFieldOfViewListener listener) {
		listener.modifyFOV(this);
	}

	@Override
	public Class<ModifyFieldOfViewListener> getListenerType() {
		return ModifyFieldOfViewListener.class;
	}
}