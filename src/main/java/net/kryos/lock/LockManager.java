package net.kryos.lock;

import java.util.ArrayList;
import java.util.List;

import net.kryos.event.impl.player.PlayerTickEvent.Post;
import net.kryos.event.impl.player.PlayerTickEvent.Pre;
import net.kryos.event.listener.impl.player.PlayerTickListener;

public class LockManager implements PlayerTickListener {
	private final List<Locker> lockers = new ArrayList<>();
	public boolean locked;

	public boolean acquire(Locker locker) {
		if(!lockers.contains(locker))
			lockers.add(locker);
		
		for (Locker l : lockers) {
	        if (l.getLockerPrivilege().value > locker.getLockerPrivilege().value) {
	            return false;
	        }
	    }
		
		return true;
	}
	
	public void free(Locker rotating) {
		if(lockers.contains(rotating))
			lockers.remove(rotating);
	}

	@Override
	public void onPre(Pre event) {
		locked = !lockers.isEmpty();
	}

	@Override
	public void onPost(Post event) {
		
	}
}