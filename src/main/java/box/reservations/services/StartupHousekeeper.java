package box.reservations.services;

import box.reservations.services.admin.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupHousekeeper {

    @Autowired
    AdminUserService adminUserService;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        adminUserService.createAdmin();
    }
}
