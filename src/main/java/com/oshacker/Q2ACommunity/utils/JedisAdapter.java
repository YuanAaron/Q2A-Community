package com.oshacker.Q2ACommunity.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger= LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool=new JedisPool("redis://127.0.0.1:6379/2");
    }

    public Long sadd(String key,String value) {
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.sadd(key,value);
        } catch (Exception e) {
            logger.error("发生异常1");
        } finally {
            if (jedis!=null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public Long srem(String key,String value) {
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.srem(key,value);
        } catch (Exception e) {
            logger.error("发生异常2");
        } finally {
            if (jedis!=null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public Long scard(String key) {
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常3");
        } finally {
            if (jedis!=null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public boolean sismember(String key,String value) {
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.sismember(key,value);
        } catch (Exception e) {
            logger.error("发生异常4");
        } finally {
            if (jedis!=null) {
                jedis.close();
            }
        }
        return false;
    }

    public Long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常5" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常6" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Jedis getJedis() {
        return pool.getResource();
    }

    public Transaction multi(Jedis jedis) {
        try {
            return jedis.multi();
        } catch (Exception e) {
            logger.error("发生异常7" + e.getMessage());
        }
        return null;
    }

    public List<Object> exec(Transaction tx, Jedis jedis) {
        try {
            return tx.exec();
        } catch (Exception e) {
            logger.error("发生异常8" + e.getMessage());
            tx.discard();
        } finally {
            if (tx != null) {
                try {
                    tx.close();
                } catch (IOException ioe) {
                    logger.error("发生异常9" + ioe.getMessage());
                }
            }

            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Long zadd(String key,double score,String value) {
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.zadd(key,score,value);
        } catch (Exception e) {
            logger.error("发生异常10"+e.getMessage());
        } finally {
            if (jedis!=null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public Long zrem(String key,String value) {
        Jedis jedis=null;
        try {
            jedis=pool.getResource();
            return jedis.zrem(key,value);
        } catch (Exception e) {
            logger.error("发生异常11"+e.getMessage());
        } finally {
            if (jedis!=null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start,end);
        } catch (Exception e) {
            logger.error("发生异常12" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常13" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0L;
    }

    public Double zscore(String key,String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,member);
        } catch (Exception e) {
            logger.error("发生异常14" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public List<String> lrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常15" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
