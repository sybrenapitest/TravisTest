package integration;

import org.shboland.persistence.db.repo.PersonRepository;
import org.shboland.persistence.db.hibernate.bean.Person;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.shboland.domain.entities.JsonCar;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.HttpStatus;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.shboland.api.Application;
import org.shboland.persistence.db.hibernate.bean.Car;
import org.shboland.persistence.db.repo.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class CarResourceIT {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CarRepository carRepository;

    
    @Autowired
    private PersonRepository personRepository;

     // @InjectInput

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        // @TearDownInputTop
        carRepository.deleteAll();
        
        personRepository.deleteAll();
     // @TearDownInputBottom
    }

    // @Input

    @Test
    public void testGetPerson_withCarWithPerson() throws Exception {
    
        Car car = IntegrationTestFactory.givenACarWithPerson(carRepository, personRepository);
        Person person = car.getPerson();

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/" + car.getId() + "/persons"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/persons/" + person.getId()));
    }

    @Test
    public void testGetPerson_withCarNoPerson() throws Exception {
    
        Car car = IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/" + car.getId() + "/persons"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testGetPerson_withoutCar() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/-1/persons"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeletePerson_withCarWithPersons() throws Exception {
    
        Car car = IntegrationTestFactory.givenACarWithPerson(carRepository, personRepository);
        Person person = car.getPerson();

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/cars/" + car.getId() + "/persons/" + person.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertFalse("Wrong entity link returned.",
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/" + car.getId() + "/persons"))
                        .andReturn().getResponse().getContentAsString()
                        .contains("/persons/" + person.getId()));
    }

    @Test
    public void testDeletePerson_withCarNoPersons() throws Exception {
    
        Car car = IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/cars/" + car.getId() + "/persons/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeletePerson_withoutCar() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("cars/-1/persons/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        

        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/-1/persons/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    
    @Test
    public void testPutPerson_withCar() throws Exception {

        Car car = IntegrationTestFactory.givenACar(carRepository);
        Person person = IntegrationTestFactory.givenAPerson(personRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/cars/" + car.getId() + "/persons/" + person.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertTrue("Wrong entity link returned.",
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/" + car.getId() + "/persons"))
                        .andReturn().getResponse().getContentAsString()
                        .contains("/persons/" + person.getId()));
    }
    
    @Test
    public void testPutPerson_withCarWithPerson() throws Exception {
    
        Car car = IntegrationTestFactory.givenACarWithPerson(carRepository, personRepository);
        Person person = car.getPerson();

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/cars/" + car.getId() + "/persons/" + person.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertTrue("Wrong entity link returned.",
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/" + car.getId() + "/persons"))
                        .andReturn().getResponse().getContentAsString()
                        .contains("/persons/" + person.getId()));
    }

    @Test
    public void testPutPerson_withCarNoPerson() throws Exception {
    
        Car car = IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/cars/" + car.getId() + "/persons/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testPutPerson_withoutCar() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/cars/-1/persons/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testGetCar_withCar() throws Exception {

        Car car = IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/" + car.getId()))
                        .andReturn().getResponse();
                        
        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/cars/" + car.getId()));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"automatic\":" + car.getAutomatic()));
        // @FieldInputAssert
    }

    @Test
    public void testGetCar_withoutCar() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/cars/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testList_withoutCars() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/cars"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong grand total returned.", response.getContentAsString().contains("\"grandTotal\":0"));
        assertTrue("Wrong number of results returned.", response.getContentAsString().contains("\"numberOfResults\":0"));
        assertTrue("Wrong entities returned.", response.getContentAsString().contains("\"results\":[]"));
    }

    @Test
    public void testList_withCars() throws Exception {
    
        Car savedCar = IntegrationTestFactory.givenACar(carRepository);
        IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/cars"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong grand total returned.", response.getContentAsString().contains("\"grandTotal\":2"));
        assertTrue("Wrong number of results returned.", response.getContentAsString().contains("\"numberOfResults\":2"));
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("cars/" + savedCar.getId()));
    }

    @Test
    public void testPostCar_invalidObject() throws Exception {
    
         MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.post("/cars"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testPostCar_newObject() throws Exception {
    
        JsonCar car = IntegrationTestFactory.givenAJsonCar();

        MockHttpServletResponse response =
                mockMvc.perform(IntegrationTestUtils.doPost("/cars", car))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.CREATED.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/cars/"));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"automatic\":" + car.getAutomatic()));
        // @FieldInputAssert
    }

    @Test
    public void testPutCar_invalidObject() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/cars/-1", new Object()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testPutCar_newObject() throws Exception {
    
        JsonCar car = IntegrationTestFactory.givenAJsonCar();

        MockHttpServletResponse response =
                mockMvc.perform(IntegrationTestUtils.doPut("/cars/-1", car))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/cars/"));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"automatic\":" + car.getAutomatic()));
        // @FieldInputAssert
    }

    @Test
    public void testPutCar_updateObject() throws Exception {
    
        Car savedCar = IntegrationTestFactory.givenACar(carRepository);

        JsonCar car = IntegrationTestFactory.givenAJsonCar();

        MockHttpServletResponse response =
                mockMvc.perform(IntegrationTestUtils.doPut("/cars/" + savedCar.getId(), car))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/cars/"));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"automatic\":" + car.getAutomatic()));
        // @FieldInputAssert
    }

    @Test
    public void testDeleteCar_unknownObject() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/cars/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteCar_deleteObject() throws Exception {
    
        Car savedCar = IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/cars/" + savedCar.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertFalse("Entity not deleted", carRepository.findById(savedCar.getId()).isPresent());
    }

}
