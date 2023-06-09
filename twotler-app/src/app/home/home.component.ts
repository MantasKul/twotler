import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { SignupComponent } from '../signup/signup.component';
import { LoginComponent } from '../login/login.component';
import { UserService } from '../services/user.service';
import { GlobalConstants } from '../shared/global-constants';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  logedin = localStorage.getItem('token');

  constructor(private dialog: MatDialog,
    private userService: UserService) { }

  ngOnInit(): void {
  }

  onSignup(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "auto";
    this.dialog.open(SignupComponent, dialogConfig);
  }

  onLogin(): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "auto";
    this.dialog.open(LoginComponent, dialogConfig);
  }

  onLogout(): void {
    localStorage.clear();
  }
}
