package com.grsoft.ads.utils;

import java.util.concurrent.locks.Lock;

public interface LockOwner {
	Lock getLock();
}
