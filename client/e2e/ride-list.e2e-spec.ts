import {RidePage} from './ride-list.po';
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




describe('Ride list', () => {
  let page: RidePage;

  beforeEach(() => {
    page = new RidePage();
  });

  it('should get and highlight Rides title attribute ', () => {
    page.navigateTo();
    expect(page.getRideTitle()).toEqual('Available Ride Listings');
  });


// For examples testing modal dialog related things, see:
// https://code.tutsplus.com/tutorials/getting-started-with-end-to-end-testing-in-angular-using-protractor--cms-29318
// https://github.com/blizzerand/angular-protractor-demo/tree/final

  it('Should have an add ride button', () => {
    page.navigateTo();
    expect(page.elementExistsWithId('addNewRide')).toBeTruthy();
  });

  it('Should open a dialog box when add ride button is clicked', () => {
    page.navigateTo();
    expect(page.elementExistsWithCss('add-ride')).toBeFalsy('There should not be a modal window yet');
    page.click('addNewRide');
    expect(page.elementExistsWithCss('add-ride')).toBeTruthy('There should be a modal window now');
  });





  describe('Add Ride', () => {

    beforeEach(() => {
      page.navigateTo();
      page.click('addNewRide');
    });

    it('Should actually add the ride with the information we put in the fields', () => {

      page.field('driverField').sendKeys('Tracy Kim');
      page.field('notesField').sendKeys('Notes');
      page.field('pickupField').sendKeys('Here');
      page.field('dropoffField').sendKeys('there');
      page.field('dateField').sendKeys('3/27/2018');
      expect(page.button('confirmAddRideButton').isEnabled()).toBe(true);
      page.click('confirmAddRideButton');

      const tracy_element = element(by.id('Tracy Kim'));
      browser.wait(protractor.ExpectedConditions.presenceOf(tracy_element), 10000);

      expect(page.getUniqueRide('Tracy Kim')).toMatch('Tracy Kim.*'); // toEqual('Tracy Kim');
    });




    describe('Add Ride (Validation)', () => {

      afterEach(() => {
        page.click('exitWithoutAddingButton');
      });

      it('Should allow us to put information into the fields of the add ride dialog', () => {
        expect(page.field('driverField').isPresent()).toBeTruthy('There should be a driver field');
        page.field('driverField').sendKeys('Dana Jones');
        expect(page.field('notesField').isPresent()).toBeTruthy('There should be a notes field');
        page.field('notesField').sendKeys('Test Notes');
        expect(page.field('pickupField').isPresent()).toBeTruthy('There should be an pickup field');
        page.field('pickupField').sendKeys('Pickup Location');
        expect(page.field('dropoffField').isPresent()).toBeTruthy('There should be an dropoff field');
        page.field('dropoffField').sendKeys('Dropoff Location');
        expect(page.field('dateField').isPresent()).toBeTruthy('There should be an date field');
        page.field('dateField').sendKeys('3/27/2019');
      });

      it('Should show the validation error message about the format of driver', () => {
        expect(element(by.id('driverField')).isPresent()).toBeTruthy('There should be an driver field');
        page.field('driverField').sendKeys('Don@ld Jones');
        expect(page.button('confirmAddRideButton').isEnabled()).toBe(false);
        //clicking somewhere else will make the error appear
        page.field('notesField').click();
        expect(page.getTextFromField('driver-error')).toBe('Name must contain only numbers and letters');
      });

      it('Should show the validation error message about pickup format', () => {
        expect(element(by.id('pickupField')).isPresent()).toBeTruthy('There should be an pickup field');
        page.field('driverField').sendKeys('Donald Jones');
        page.field('notesField').sendKeys('30');
        page.field('pickupField').sendKeys('donjones.com');
        expect(page.button('confirmAddRideButton').isEnabled()).toBe(false);
        //clicking somewhere else will make the error appear
        page.field('driverField').click();
        expect(page.getTextFromField('pickup-error')).toBe('Meetup location must contain only numbers and letters');
      });
    });
  });
});

