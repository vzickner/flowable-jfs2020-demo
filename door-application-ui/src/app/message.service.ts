import {Injectable} from "@angular/core";
import {BehaviorSubject, Subject} from "rxjs";
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stomp-websocket';

@Injectable()
export class MessageService {

  private messages: Subject<Message> = new BehaviorSubject(null);
  private socket = null;
  private stompClient = null;

  constructor() {
    this.socket = new SockJS("http://localhost:8081/websocket");
    this.stompClient = Stomp.over(this.socket);
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/topic/pushNotification', (notification) => {
        this.messages.next(JSON.parse(notification.body));
      });
    });
  }

  public getMessages() {
    return this.messages.asObservable();
  }

}

export interface Message {
  topic: string;
  message: any;
}
