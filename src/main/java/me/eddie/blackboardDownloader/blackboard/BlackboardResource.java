package me.eddie.blackboardDownloader.blackboard;

public class BlackboardResource<T> {
    private T res = null;
    private boolean fetched = false;
    private BlackboardResourceFetcher<T> resourceFetcher;

    public BlackboardResource(BlackboardResourceFetcher<T> resourceFetcher) {
        this.resourceFetcher = resourceFetcher;
    }

    public static interface BlackboardResourceFetcher<T> {
        public T fetch(Blackboard blackboard);
    }

    public T get(Blackboard blackboard){
        if(!fetched){
            this.res = resourceFetcher.fetch(blackboard);
            this.fetched = true;
        }
        return this.res;
    }
}
