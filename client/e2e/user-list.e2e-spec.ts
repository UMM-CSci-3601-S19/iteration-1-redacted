import {UserPage} from './user-list.po';
import {browser, protractor, element, by} from 'protractor';
import {Key} from 'selenium-webdriver';

// This line (combined with the function that follows) is here for us
// to be able to see what happens (part of slowing things down)
// https://hassantariqblog.wordpress.com/2015/11/09/reduce-speed-of-angular-e2e-protractor-tests/

const origFn = browser.driver.controlFlow().execute;

browser.driver.controlFlow().execute = function () {
    let args = arguments;

    // queue 100ms wait between test
    // This delay is only put here so that you can watch the browser do its thing.
    // If you're tired of it taking long you can remove this call or change the delay
    // to something smaller (even 0).
    origFn.call(browser.driver.controlFlow(), () => {
        return protractor.promise.delayed(100);
    });

    return origFn.apply(browser.driver.controlFlow(), args);
};


describe('User list', () => {
  let page: UserPage;

  beforeEach(() => {
    page = new UserPage();
  });

  it('should get and highlight Users title attribute ', () => {
    page.navigateTo();
    expect(page.getUserTitle()).toEqual('Albert Einstein\'s Profile');
  });

});

