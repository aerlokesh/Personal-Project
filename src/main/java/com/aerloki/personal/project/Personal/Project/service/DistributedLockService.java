package com.aerloki.personal.project.Personal.Project.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistributedLockService {
    
    private final RedissonClient redissonClient;
    
    private static final String LOCK_PREFIX = "inventory:lock:";
    private static final long WAIT_TIME = 5; // seconds
    private static final long LEASE_TIME = 10; // seconds
    
    /**
     * Executes a task with a distributed lock to prevent race conditions
     * 
     * @param productId The product ID to lock
     * @param task The task to execute while holding the lock
     * @return true if the task was executed successfully, false if lock couldn't be acquired
     */
    public boolean executeWithLock(Long productId, Runnable task) {
        String lockKey = LOCK_PREFIX + productId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // Try to acquire the lock
            boolean isLocked = lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
            
            if (isLocked) {
                try {
                    log.info("Lock acquired for product: {}", productId);
                    task.run();
                    return true;
                } finally {
                    // Always release the lock
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                        log.info("Lock released for product: {}", productId);
                    }
                }
            } else {
                log.warn("Could not acquire lock for product: {} within {} seconds", productId, WAIT_TIME);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted for product: {}", productId, e);
            return false;
        } catch (Exception e) {
            log.error("Error while executing task with lock for product: {}", productId, e);
            // Try to release lock if held
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            throw new RuntimeException("Failed to execute operation with distributed lock", e);
        }
    }
    
    /**
     * Executes a task with distributed locks for multiple products
     * 
     * @param productIds Array of product IDs to lock
     * @param task The task to execute while holding all locks
     * @return true if the task was executed successfully, false if any lock couldn't be acquired
     */
    public boolean executeWithMultipleLocks(Long[] productIds, Runnable task) {
        if (productIds == null || productIds.length == 0) {
            task.run();
            return true;
        }
        
        RLock[] locks = new RLock[productIds.length];
        
        try {
            // Acquire all locks
            for (int i = 0; i < productIds.length; i++) {
                String lockKey = LOCK_PREFIX + productIds[i];
                locks[i] = redissonClient.getLock(lockKey);
                
                boolean isLocked = locks[i].tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
                if (!isLocked) {
                    log.warn("Could not acquire lock for product: {}", productIds[i]);
                    // Release any locks we've acquired so far
                    releaseLocks(locks, i);
                    return false;
                }
            }
            
            log.info("All locks acquired for {} products", productIds.length);
            
            try {
                task.run();
                return true;
            } finally {
                releaseLocks(locks, locks.length);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted", e);
            return false;
        } catch (Exception e) {
            log.error("Error while executing task with multiple locks", e);
            throw new RuntimeException("Failed to execute operation with distributed locks", e);
        }
    }
    
    private void releaseLocks(RLock[] locks, int count) {
        for (int i = 0; i < count; i++) {
            if (locks[i] != null && locks[i].isHeldByCurrentThread()) {
                try {
                    locks[i].unlock();
                } catch (Exception e) {
                    log.error("Error releasing lock", e);
                }
            }
        }
    }
}
