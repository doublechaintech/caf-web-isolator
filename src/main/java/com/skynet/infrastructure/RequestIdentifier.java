package com.skynet.infrastructure;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

public class RequestIdentifier {
  private long createTime = System.currentTimeMillis();

  private long requestId;
  public static ThreadLocal<RequestIdentifier> instance = new ThreadLocal<>();

  public static synchronized RequestIdentifier get() {
    RequestIdentifier requestIdentifier = instance.get();
    if (requestIdentifier == null) {
      requestIdentifier = new RequestIdentifier();
      requestIdentifier.requestId = IdUtil.getSnowflakeNextId();
      instance.set(requestIdentifier);
    }
    return requestIdentifier;
  }

  public static void clear() {
    instance.set(null);
  }

  public long getRequestId() {
    return requestId;
  }

  public void setRequestId(long pRequestId) {
    requestId = pRequestId;
  }

  @Override
  public String toString() {
    return StrUtil.format("{}-{}", requestId, System.currentTimeMillis() - createTime);
  }
}
