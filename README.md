# Test access artemis by failover

1. start `SpringBootMdbApplication
2. run test `JmsTest`


--
## 驗證重新連線機制

### 啟動mq server

1. 啟動 Docker Desktop

2. 執行

- 啟動兩台 docker-compose

```bash
docker-compose up -d
```

3. run java appliction

4. 觀察log紀錄

```log
Connected to broker: XXXX
```

5. 手動停掉Mq server 確認是否有重新連線

- Docker stop

```cmd
## 61616
docker stop artemis1

## 61617
docker stop artemis2
```

5. 觀察重新連接og紀錄