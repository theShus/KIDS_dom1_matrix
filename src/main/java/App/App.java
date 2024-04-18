package App;

import logger.Logger;

public class App {

    //Else
    public static final Logger logger = new Logger();



    public void start() {
        PropertyStorage.getInstance().loadProperties();

    }
}
