package ivan.jdbcExample;

public class User {
    private Integer id;
    private String name;


    private User(final String name){
        this.id = 0;
        this.name = name;
    }

    private User(final Integer id, final String name){
        this.id = id;
        this.name = name;
    }


    public static User create(String name){
        return new User(name);
    }



    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString() {
        return String.format(
                "%s{id=%d, name='%s'}",
                getClass().getSimpleName(), id, name
        );
    }

}



