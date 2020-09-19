import {Component} from '@angular/core';
import {MessageService} from "./message.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {BehaviorSubject} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  public title = 'Monitor Dashboard';
  public messageListSubject = new BehaviorSubject([]);
  public door = new BehaviorSubject('door-close.gif');
  private messageList = [];

  constructor(
    private messageService: MessageService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {
    this.messageService.getMessages()
      .subscribe((message) => {
        if (message == null) {
          return;
        }
        this.snackBar.open('Message archived through "' + message.topic + '": ' + JSON.stringify(message.message));
        this.messageList.splice(0, 0, {
          date: new Date(),
          topic: message.topic,
          content: JSON.stringify(message.message, null, 2)
        })
        this.messageListSubject.next([...this.messageList]);
        if (message.topic == 'gate-action-topic') {
          if (message.message.status == 'Open') {
            this.door.next('door-open.gif?time=' + new Date().getTime());
          } else {
            this.door.next('door-close.gif?time=' + new Date().getTime());
          }
        }
      })
  }

}
