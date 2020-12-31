package me.eddie.blackboardDownloader.blackboard;

import me.eddie.blackboardDownloader.http.HttpClient;

public class RetryingBlackboardResourceFetcher<T> implements BlackboardResource.BlackboardResourceFetcher<T> {

    public static interface FailableResourceFetcher<T> {
        public HttpClient.Response<T> fetch(Blackboard blackboard);
    }

    private int tries = 4;
    private FailableResourceFetcher<T> fetcher;

    public RetryingBlackboardResourceFetcher(int tries, FailableResourceFetcher<T> fetcher) {
        this.tries = tries;
        this.fetcher = fetcher;
    }

    @Override
    public T fetch(Blackboard blackboard) {
        for(int i=0;i<tries-1;i++){
            try {
                HttpClient.Response<T> resp = fetcher.fetch(blackboard);
                if (resp.getResponse() == null || resp.getHttpCode() != 200) {
                    System.out.println("Failed to get resource, re-opening blackboard and trying again...");
                    blackboard.open(); //Make sure authenticated...
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    return resp.getResponse();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
