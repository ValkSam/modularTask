package modular.design.service;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public abstract class RootDirProcessorFactory {

    @Lookup
    public abstract RootDirProcessingTask getRootDirProcessingTask(String dir);

}
