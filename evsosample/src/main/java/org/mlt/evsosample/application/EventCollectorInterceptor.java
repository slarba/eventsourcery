package org.mlt.evsosample.application;

import org.mlt.eso.Events;
import org.mlt.eso.serialization.StorableEvent;
import org.mlt.eso.stores.JDBCEventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class EventCollectorInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JDBCEventStore eventStore;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Events.beginCollect();
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        List<StorableEvent> events = Events.endCollect();
        eventStore.append(events);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        if(ex!=null) {
            Events.endCollect();
        }
    }
}
