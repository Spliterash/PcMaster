package ru.spliterash.pcmasterserver.api.methods.self;

import org.springframework.stereotype.Component;
import ru.spliterash.pcmasterserver.api.methods.AbstractExecutor;
import ru.spliterash.pcmasterserver.secutiry.PcMasterUser;

@Component
public class SelfExecutor extends AbstractExecutor<Object, PcMasterUser> {
    @Override
    public PcMasterUser execute(Object json, PcMasterUser user) {
        return user;
    }
}
