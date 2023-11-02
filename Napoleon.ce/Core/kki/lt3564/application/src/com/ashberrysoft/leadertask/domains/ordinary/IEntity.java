package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.UUID;

/**
 * интерфейс для domains-классов
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public interface IEntity extends Serializable {

    public UUID getId();

    public long getUsn();
}
