package io.github.altriaaa.huluwarogue.network;

public class RequestMessage
{
    public String type = "request";
    public REQ_KIND kind;

    public enum REQ_KIND
    {
        LOAD,
        SAVE,
        EXIT
    }
}
