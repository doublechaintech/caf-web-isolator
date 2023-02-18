package com.terapico.caf;

import com.terapico.uccaf.BaseUserContext;

import javax.servlet.http.HttpServletRequest;

public interface AppInvocationCustomizer {
  Object exec(BaseUserContext  ctx, HttpServletRequest request);
}
